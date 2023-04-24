package org.rhasspy.mobile.viewmodel.settings.microphoneoverlay

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SelectMicrophoneOverlaySizeOption
import org.rhasspy.mobile.viewmodel.settings.microphoneoverlay.MicrophoneOverlaySettingsUiEvent.Change.SetMicrophoneOverlayWhileAppEnabled

@Stable
class MicrophoneOverlaySettingsViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(MicrophoneOverlaySettingsViewState())
    val viewState = _viewState.readOnly

    fun onEvent(event: MicrophoneOverlaySettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                is SelectMicrophoneOverlaySizeOption -> {
                    AppSetting.microphoneOverlaySizeOption.value = change.option
                    it.copy(microphoneOverlaySizeOption = change.option)
                }

                is SetMicrophoneOverlayWhileAppEnabled -> {
                    AppSetting.isMicrophoneOverlayWhileAppEnabled.value = change.enabled
                    it.copy(isMicrophoneOverlayWhileAppEnabled = change.enabled)
                }
            }
        }
    }

}