package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class AudioPlayingConfigurationViewState internal constructor(
    val audioPlayingOption: AudioPlayingOption = ConfigurationSetting.audioPlayingOption.value,
    val audioOutputOption: AudioOutputOption = ConfigurationSetting.audioOutputOption.value,
    val isUseCustomAudioPlayingHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value,
    val audioPlayingHttpEndpoint: String = ConfigurationSetting.audioPlayingHttpEndpoint.value,
    val audioPlayingMqttSiteId: String = ConfigurationSetting.audioPlayingMqttSiteId.value
) : IConfigurationEditViewState() {

    val audioPlayingOptionList: ImmutableList<AudioPlayingOption> = AudioPlayingOption.values().toImmutableList()
    val audioOutputOptionList: ImmutableList<AudioOutputOption> = AudioOutputOption.values().toImmutableList()

    override val isTestingEnabled: Boolean get() = audioPlayingOption != AudioPlayingOption.Disabled

}