package org.rhasspy.mobile.logic.domains.speechtotext

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import okio.FileHandle
import okio.Path
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.Record
import org.rhasspy.mobile.data.connection.HttpClientResult
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.Disabled
import org.rhasspy.mobile.data.service.ServiceState.Success
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.rhasspy2hermes.IRhasspy2HermesConnection
import org.rhasspy.mobile.logic.domains.voiceactivitydetection.IVoiceActivityDetectionService
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocusService
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioService
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrTextCaptured
import org.rhasspy.mobile.logic.middleware.Source.Local
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.appendWavHeader
import org.rhasspy.mobile.platformspecific.audiorecorder.AudioRecorderUtils.getWavHeader
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.platformspecific.extensions.commonDelete
import org.rhasspy.mobile.platformspecific.extensions.commonInternalFilePath
import org.rhasspy.mobile.platformspecific.extensions.commonReadWrite
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting

interface ISpeechToTextService : IService {

    override val serviceState: StateFlow<ServiceState>

    val speechToTextAudioFile: Path
    val isActive: Boolean
    val isRecording: StateFlow<Boolean>

    fun endSpeechToText(sessionId: String, fromMqtt: Boolean)
    fun startSpeechToText(sessionId: String, fromMqtt: Boolean)

}

/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
internal class SpeechToTextService(
    paramsCreator: SpeechToTextServiceParamsCreator,
    private val audioRecorder: IAudioRecorder
) : ISpeechToTextService {

    private val logger = Logger.withTag("SpeechToTextService")

    private val audioFocusService by inject<IAudioFocusService>()
    private val mqttClientService by inject<IMqttConnection>()
    private val nativeApplication by inject<NativeApplication>()
    private val serviceMiddleware by inject<IServiceMiddleware>()
    private val localAudioService by inject<ILocalAudioService>()
    private val voiceActivityDetectionService by inject<IVoiceActivityDetectionService> { parametersOf(audioRecorder) }
    private val httpClientConnection by inject<IRhasspy2HermesConnection>()


    private val paramsFlow: StateFlow<SpeechToTextServiceParams> = paramsCreator()
    private val params: SpeechToTextServiceParams get() = paramsFlow.value

    private val _serviceState = MutableStateFlow<ServiceState>(Success)
    override val serviceState = _serviceState.readOnly

    override val speechToTextAudioFile: Path = Path.commonInternalFilePath(nativeApplication, "SpeechToTextAudio.wav")
    override var isActive: Boolean = false
    override val isRecording: StateFlow<Boolean> = audioRecorder.isRecording
    private var fileHandle: FileHandle? = null

    private val scope = CoroutineScope(Dispatchers.IO)
    private var recorder: Job? = null

    init {
        scope.launch {
            paramsFlow.collect {
                recorder?.cancel()
                recorder = null
                setupState()
            }
        }
    }

    private fun setupState() {
        _serviceState.value = when (params.speechToTextOption) {
            SpeechToTextOption.Rhasspy2HermesHttp -> Success
            SpeechToTextOption.Rhasspy2HermesMQTT -> Success
            SpeechToTextOption.Disabled           -> Disabled
        }
    }

    /**
     * Speech to Text (Wav Data)
     * used to translate last spoken
     *
     * HTTP:
     * - calls service to translate speech to text, then handles the intent if dialogue manager is set to local
     *
     * RemoteMQTT
     * - audio was already send to mqtt while recording in audioFrame
     *
     * fromMqtt is used to check if silence was detected by remote mqtt device
     */
    override fun endSpeechToText(sessionId: String, fromMqtt: Boolean) {
        if (!isActive) return
        isActive = false
        voiceActivityDetectionService.stop()
        logger.d { "endSpeechToText sessionId: $sessionId fromMqtt $fromMqtt" }

        audioRecorder.stopRecording()

        //stop recorder
        recorder?.cancel()
        recorder = null

        audioFocusService.abandon(Record)

        //add wav header to file
        val header = getWavHeader(
            audioRecorderChannelType = params.audioOutputChannelType,
            audioRecorderEncodingType = params.audioOutputEncodingType,
            audioRecorderSampleRateType = params.audioOutputSampleRateType,
            audioSize = fileHandle?.size() ?: 0
        )

        fileHandle?.write(0, header, 0, header.size)
        fileHandle?.flush()
        fileHandle?.close()
        fileHandle = null

        //evaluate result
        when (params.speechToTextOption) {
            SpeechToTextOption.Rhasspy2HermesHttp -> {
                httpClientConnection.speechToText(speechToTextAudioFile) { result ->
                    _serviceState.value = result.toServiceState()
                    val action = when (result) {
                        is HttpClientResult.Error      -> AsrError(Local)
                        is HttpClientResult.Success    -> AsrTextCaptured(Local, result.data)
                        is HttpClientResult.KnownError -> AsrError(Local)
                    }
                    serviceMiddleware.action(action)
                }

            }

            SpeechToTextOption.Rhasspy2HermesMQTT -> if (!fromMqtt) {
                mqttClientService.stopListening(sessionId) {
                    _serviceState.value = it
                }
            }

            SpeechToTextOption.Disabled -> Unit
        }
    }

    override fun startSpeechToText(sessionId: String, fromMqtt: Boolean) {
        if (isActive) return
        isActive = true
        logger.d { "startSpeechToText sessionId: $sessionId fromMqtt $fromMqtt" }

        //clear data
        recorder?.cancel()
        recorder = null

        fileHandle?.flush()
        fileHandle?.close()
        speechToTextAudioFile.commonDelete()
        fileHandle = speechToTextAudioFile.commonReadWrite()


        when (params.speechToTextOption) {
            SpeechToTextOption.Rhasspy2HermesHttp -> _serviceState.value = Success
            SpeechToTextOption.Rhasspy2HermesMQTT -> if (!fromMqtt) mqttClientService.startListening(sessionId) { _serviceState.value = it }
            SpeechToTextOption.Disabled           -> {
                _serviceState.value = Disabled
                return //recorder doesn't need to be started
            }
        }

        voiceActivityDetectionService.start()

        //start recorder
        recorder = scope.launch {
            record(sessionId, this)
        }
    }

    private fun audioFrame(sessionId: String, data: ByteArray) {
        if (!isActive) return

        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "audioFrame dataSize: ${data.size}" }
        }

        when (params.speechToTextOption) {
            SpeechToTextOption.Rhasspy2HermesHttp -> _serviceState.value = Success
            SpeechToTextOption.Rhasspy2HermesMQTT -> {
                mqttClientService.asrAudioSessionFrame(
                    sessionId = sessionId,
                    data.appendWavHeader(
                        audioRecorderChannelType = params.audioOutputChannelType,
                        audioRecorderEncodingType = params.audioOutputEncodingType,
                        audioRecorderSampleRateType = params.audioOutputSampleRateType
                    )
                ) { _serviceState.value = it }
            }

            SpeechToTextOption.Disabled -> _serviceState.value = Disabled
        }

        //write async after data was send
        fileHandle?.write(
            fileOffset = fileHandle?.size() ?: 0,
            array = data,
            arrayOffset = 0,
            byteCount = data.size
        )
    }

    private suspend fun record(sessionId: String, coroutineScope: CoroutineScope) {
        localAudioService.isPlayingState.first { !it }

        audioFocusService.request(Record)

        coroutineScope.launch {
            //collect from audio recorder
            audioRecorder.output.collectLatest { data ->
                if (!localAudioService.isPlayingState.value) {
                    if (data.isNotEmpty()) {
                        audioFrame(sessionId, data)
                    }
                }
            }
        }

        audioRecorder.startRecording(
            audioRecorderChannelType = params.audioRecorderChannelType,
            audioRecorderEncodingType = params.audioRecorderEncodingType,
            audioRecorderSampleRateType = params.audioRecorderSampleRateType,
            audioRecorderOutputChannelType = params.audioOutputChannelType,
            audioRecorderOutputEncodingType = params.audioOutputEncodingType,
            audioRecorderOutputSampleRateType = params.audioOutputSampleRateType,
            isAutoPauseOnMediaPlayback = params.isAutoPauseOnMediaPlayback,
        )
    }

}