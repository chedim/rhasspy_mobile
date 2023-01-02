package org.rhasspy.mobile.services.localaudio

import org.rhasspy.mobile.MR
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.sounds.SoundOption
import org.rhasspy.mobile.viewmodel.settings.sound.SoundFileFolder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocalAudioService : IService() {
    private val logger = LogType.LocalAudioService.logger()

    private val audioPlayer = AudioPlayer()
    val isPlayingState = audioPlayer.isPlayingState

    override fun onClose() {
        logger.d { "onClose" }
        audioPlayer.stop()
        audioPlayer.close()
    }

    suspend fun playAudio(data: List<Byte>): Unit = suspendCoroutine { continuation ->
        logger.d { "playAudio ${data.size}" }
        audioPlayer.playData(
            data = data,
            volume = AppSetting.volume.value,
            onFinished = {
                logger.d { "onFinished" }
                continuation.resume(Unit)
            },
            onError = { exception ->
                exception?.also {
                    logger.e(it) { "onError" }
                } ?: run {
                    logger.e { "onError" }
                }
                continuation.resume(Unit)
            }
        )
    }

    fun playWakeSound() {
        logger.d { "playWakeSound" }
        when (AppSetting.wakeSound.value) {
            SoundOption.Disabled.name -> {}
            SoundOption.Default.name -> audioPlayer.playFileResource(
                MR.files.etc_wav_beep_hi,
                AppSetting.wakeSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
            else -> audioPlayer.playSoundFile(
                "${SoundFileFolder.Wake}/${AppSetting.wakeSound.value}",
                AppSetting.wakeSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
        }
    }

    fun playRecordedSound() {
        logger.d { "playRecordedSound" }
        when (AppSetting.recordedSound.value) {
            SoundOption.Disabled.name -> {}
            SoundOption.Default.name -> audioPlayer.playFileResource(
                MR.files.etc_wav_beep_lo,
                AppSetting.recordedSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
            else -> audioPlayer.playSoundFile(
                "${SoundFileFolder.Wake}/${AppSetting.recordedSound.value}",
                AppSetting.recordedSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
        }
    }

    fun playErrorSound() {
        logger.d { "playErrorSound" }
        when (AppSetting.errorSound.value) {
            SoundOption.Disabled.name -> {}
            SoundOption.Default.name -> audioPlayer.playFileResource(
                MR.files.etc_wav_beep_error,
                AppSetting.errorSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
            else -> audioPlayer.playSoundFile(
                "${SoundFileFolder.Error}/${AppSetting.errorSound.value}",
                AppSetting.errorSoundVolume.data,
                AppSetting.soundIndicationOutputOption.value
            )
        }
    }

    fun stop() {
        logger.d { "stop" }
        audioPlayer.stop()
    }

}