package org.rhasspy.mobile.data.audiorecorder

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.IOption
import org.rhasspy.mobile.resources.MR

actual enum class AudioFormatChannelType(
    override val text: StableStringResource,
    actual val value: Int,
    actual val count: Int
) : IOption<AudioFormatChannelType> {
    //TODO #509
    Default(MR.strings.defaultText.stable, 1, 1);

    override val internalEntries get() = entries

    actual companion object {
        actual val default: AudioFormatChannelType get() = Default
    }

}