package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.services.native.AudioRecorder
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.settings.AppSettings
import kotlin.native.concurrent.ThreadLocal
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

/**
 * Handles listening to speech
 */
@ThreadLocal
object RecordingService {
    private val logger = Logger.withTag(this::class.simpleName!!)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val listening = MutableLiveData(false)

    //represents listening Status for ui
    val status: LiveData<Boolean> = listening.map { it }

    private var data = mutableListOf<Byte>()

    private var firstSilenceDetected: Instant? = null

    private var job: Job? = null

    //https://stackoverflow.com/questions/19145213/android-audio-capture-silence-detection
    private fun searchThreshold(arr: ByteArray, thr: Int): Boolean {
        arr.forEach {
            if (it >= thr || it <= -thr) {
                return true
            }
        }
        return false
    }

    /**
     * should be called when wake word is detected or user wants to speak
     * by clicking ui
     */
    fun startRecording() {
        logger.d { "startRecording" }
        firstSilenceDetected = null
        listening.value = true
        data.clear()
        indication()

        job = coroutineScope.launch {
            AudioRecorder.output.collectIndexed { _, value ->
                data.addAll(value.toList())

                if (AppSettings.isAutomaticSilenceDetection.data) {
                    if (!searchThreshold(value, AppSettings.automaticSilenceDetectionAudioLevel.data)) {
                        if (firstSilenceDetected == null) {
                            firstSilenceDetected = Clock.System.now()

                        } else if (firstSilenceDetected?.minus(Clock.System.now()) ?: ZERO <
                            (-AppSettings.automaticSilenceDetectionTime.data).milliseconds
                        ) {
                            logger.i { "diff ${firstSilenceDetected?.minus(Clock.System.now())}" }

                            CoroutineScope(Dispatchers.Main).launch {
                                //stop instantly
                                listening.value = false
                                ServiceInterface.stopRecording()
                            }
                        }
                    }
                }
            }
        }

        AudioRecorder.startRecording()
    }

    /**
     * called when service should stop listening
     */
    fun stopRecording() {
        logger.d { "stopRecording" }

        listening.value = false
        stopIndication()
        AudioRecorder.stopRecording()
        job?.cancel()
    }

    /**
     * starts wake word indication according to settings
     */
    private fun indication() {
        logger.d { "indication" }

        if (AppSettings.isWakeWordSoundIndication.data) {
            NativeIndication.playAudio(MR.files.etc_wav_beep_hi)
        }

        if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data) {
            NativeIndication.wakeUpScreen()
        }

        if (AppSettings.isWakeWordLightIndication.data) {
            NativeIndication.showIndication()
        }
    }

    /**
     * stops all indications
     */
    private fun stopIndication() {
        logger.d { "stopIndication" }

        NativeIndication.closeIndicationOverOtherApps()
        NativeIndication.releaseWakeUp()
    }

    fun getLatestRecording() = data.toByteArray()

}