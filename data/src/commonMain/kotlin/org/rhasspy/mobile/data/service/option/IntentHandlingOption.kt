package org.rhasspy.mobile.data.service.option

import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

enum class IntentHandlingOption(override val text: StableStringResource) : IOption<IntentHandlingOption> {

    HomeAssistant(MR.strings.homeAssistant.stable),
    Rhasspy2HermesHttp(MR.strings.rhasspy2hermes_http.stable),
    WithRecognition(MR.strings.withRecognition.stable),
    Disabled(MR.strings.disabled.stable);

    override val internalEntries get() = entries

}