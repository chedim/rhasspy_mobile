package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

enum class AudioPlayingOption(override val text: StableStringResource) : IOption<AudioPlayingOption> {

    Local(MR.strings.local.stable),
    Rhasspy2HermesHttp(MR.strings.rhasspy2hermes_http.stable),
    Rhasspy2HermesMQTT(MR.strings.rhasspy2hermes_mqtt.stable),
    Disabled(MR.strings.disabled.stable);

    override val internalEntries get() = entries

}