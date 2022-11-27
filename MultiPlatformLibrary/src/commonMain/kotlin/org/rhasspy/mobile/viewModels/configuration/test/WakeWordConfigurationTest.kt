package org.rhasspy.mobile.viewModels.configuration.test

import org.koin.core.component.get
import org.rhasspy.mobile.services.hotword.HotWordService

class WakeWordConfigurationTest : IConfigurationTest() {

    public fun startTest() {
        //used for wake word recording
        get<HotWordService>().startDetection()

    }

    override fun onClose() {
        //TODO("Not yet implemented")
    }
}