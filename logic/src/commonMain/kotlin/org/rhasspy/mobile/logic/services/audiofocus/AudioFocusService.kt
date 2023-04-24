package org.rhasspy.mobile.logic.services.audiofocus

import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason.*
import org.rhasspy.mobile.data.audiofocus.AudioFocusType
import org.rhasspy.mobile.platformspecific.media.AudioFocusUtil
import org.rhasspy.mobile.settings.AppSetting

class AudioFocusService {

    private var reason: AudioFocusRequestReason? = null

    private fun shouldRequest(reason: AudioFocusRequestReason): Boolean {
        return when (reason) {
            Notification -> AppSetting.isAudioFocusOnNotification.value
            Sound -> AppSetting.isAudioFocusOnSound.value
            Record -> AppSetting.isAudioFocusOnRecord.value
            Dialog -> AppSetting.isAudioFocusOnDialog.value
        }
    }

    fun request(reason: AudioFocusRequestReason) {
        if (AppSetting.audioFocusOption.value != AudioFocusType.Disabled) {

            if (shouldRequest(reason)) {
                this.reason = reason
                AudioFocusUtil.request(reason, AppSetting.audioFocusOption.value)
            }

        }
    }

    fun abandon(reason: AudioFocusRequestReason) {
        if (AppSetting.audioFocusOption.value != AudioFocusType.Disabled) {

            if (shouldRequest(reason) && (reason.ordinal > (this.reason?.ordinal ?: -1))) {
                this.reason = null
                AudioFocusUtil.abandon(reason, AppSetting.audioFocusOption.value)
            }

        }
    }

}