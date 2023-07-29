package org.rhasspy.mobile.logic.services.wakeword

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.WakeWordDetected
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.recording.IRecordingService
import org.rhasspy.mobile.platformspecific.porcupine.PorcupineWakeWordClient
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting

interface IWakeWordService : IService {

    override val serviceState: StateFlow<ServiceState>

    val isRecording: StateFlow<Boolean>

    fun startDetection()
    fun stopDetection()

}

/**
 * hot word services listens for hot word, evaluates configuration settings but no states
 *
 * calls stateMachineService when hot word was detected
 */
internal class WakeWordService(
    paramsCreator: WakeWordServiceParamsCreator,
) : IWakeWordService {

    override val logger = LogType.WakeWordService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    override val serviceState = _serviceState.readOnly

    private val recordingService by inject<IRecordingService>()
    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val paramsFlow: StateFlow<WakeWordServiceParams> = paramsCreator()
    private val params: WakeWordServiceParams get() = paramsFlow.value

    private var udpConnection: UdpConnection? = null
    private var porcupineWakeWordClient: PorcupineWakeWordClient? = null

    private val _isRecording = MutableStateFlow(false)
    override val isRecording = _isRecording.readOnly

    private var isDetectionRunning = false

    private val scope = CoroutineScope(Dispatchers.Default)
    private var recording: Job? = null

    /**
     * starts the service
     */
    init {
        scope.launch {
            paramsFlow.collect {
                stop()
                initialize()
                if (isDetectionRunning) {
                    startDetection()
                }
            }
        }
    }

    private fun initialize() {
        logger.d { "initialization" }
        _serviceState.value = when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {
                _serviceState.value = ServiceState.Loading
                //when porcupine is used for hotWord then start local service
                porcupineWakeWordClient = PorcupineWakeWordClient(
                    params.isUseCustomRecorder,
                    params.audioRecorderSampleRateType,
                    params.audioRecorderChannelType,
                    params.audioRecorderEncodingType,
                    params.wakeWordPorcupineAccessToken,
                    params.wakeWordPorcupineKeywordDefaultOptions,
                    params.wakeWordPorcupineKeywordCustomOptions,
                    params.wakeWordPorcupineLanguage,
                    ::onKeywordDetected,
                    ::onClientError
                )

                checkPorcupineInitialized()
            }
            //when mqtt is used for hotWord, start recording, might already recording but then this is ignored
            WakeWordOption.MQTT      -> ServiceState.Success
            WakeWordOption.Udp       -> {
                _serviceState.value = ServiceState.Loading

                udpConnection = UdpConnection(
                    params.wakeWordUdpOutputHost,
                    params.wakeWordUdpOutputPort
                )

                checkUdpConnection()
            }

            WakeWordOption.Disabled  -> ServiceState.Disabled
        }
    }

    private fun stop() {
        logger.d { "stop" }
        recording?.cancel()
        recording = null
        porcupineWakeWordClient?.close()
        porcupineWakeWordClient = null
        udpConnection?.close()
        udpConnection = null
    }

    private fun onClientError(exception: Exception) {
        _serviceState.value = ServiceState.Exception(exception)
        logger.e(exception) { "porcupineError" }
    }

    override fun startDetection() {
        logger.d { "startDetection $isDetectionRunning" }
        isDetectionRunning = true

        if (!params.isEnabled) return

        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> {

                if (porcupineWakeWordClient == null) {
                    initialize()
                } else {
                    checkPorcupineInitialized()
                }

                porcupineWakeWordClient?.also {
                    _isRecording.value = true
                    val error = porcupineWakeWordClient?.start()
                    _serviceState.value = error?.let {
                        logger.e(it) { "porcupineError" }
                        ServiceState.Exception(it)
                    } ?: ServiceState.Success
                }

            }

            WakeWordOption.MQTT      -> Unit //nothing will wait for mqtt message
            WakeWordOption.Udp       -> {

                if (udpConnection == null) {
                    initialize()
                } else {
                    checkUdpConnection()
                }

                _isRecording.value = true
                //collect audio from recorder
                if (recording == null) {
                    recording = scope.launch {
                        recordingService.output.collect(::hotWordAudioFrame)
                    }
                }
            }

            WakeWordOption.Disabled  -> Unit
        }
    }

    private suspend fun hotWordAudioFrame(data: ByteArray) {
        if (AppSetting.isLogAudioFramesEnabled.value) {
            logger.d { "hotWordAudioFrame dataSize: ${data.size}" }
        }
        when (params.wakeWordOption) {
            WakeWordOption.Porcupine -> Unit
            WakeWordOption.MQTT      -> Unit //nothing will wait for mqtt message
            WakeWordOption.Udp       -> {

                if (udpConnection == null) {
                    initialize()
                }

                _serviceState.value = checkUdpConnection()

                scope.launch {
                    //needs to be called async else native Audio Recorder stops working
                    udpConnection?.streamAudio(data)
                }
            }

            WakeWordOption.Disabled  -> Unit
        }
    }

    override fun stopDetection() {
        if (!isDetectionRunning) return

        logger.d { "stopDetection" }
        _isRecording.value = false
        recording?.cancel()
        recording = null
        porcupineWakeWordClient?.stop()
        porcupineWakeWordClient?.close()
        porcupineWakeWordClient = null
        isDetectionRunning = false
    }

    private fun onKeywordDetected(hotWord: String) {
        logger.d { "onKeywordDetected $hotWord" }
        serviceMiddleware.action(WakeWordDetected(Source.Local, hotWord))
    }

    private fun checkUdpConnection(): ServiceState {
        return udpConnection?.let { connection ->
            if (!connection.isConnected) {
                connection.connect()?.let {
                    ServiceState.Exception(it)
                } ?: ServiceState.Success
            } else ServiceState.Success
        } ?: run {
            logger.e { "udpConnection not initialized" }
            ServiceState.Exception(Exception("udpConnection not initialized"))
        }
    }

    private fun checkPorcupineInitialized(): ServiceState {
        return porcupineWakeWordClient?.let { client ->
            if (!client.isInitialized) {
                val error = porcupineWakeWordClient?.initialize()
                error?.let { exception ->
                    logger.e(exception) { "porcupine error" }
                    porcupineWakeWordClient = null
                    ServiceState.Exception(exception)
                } ?: ServiceState.Success
            } else ServiceState.Success
        } ?: run {
            logger.e { "porcupineWakeWordClient not initialized" }
            ServiceState.Exception(Exception("porcupineWakeWordClient not initialized"))
        }
    }

}