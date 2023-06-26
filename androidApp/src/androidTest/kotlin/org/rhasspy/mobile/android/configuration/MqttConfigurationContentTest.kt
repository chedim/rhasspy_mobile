package org.rhasspy.mobile.android.configuration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.MqttConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.SetMqttEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.SetMqttSSLEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationViewModel
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MqttConfigurationContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createComposeRule()

    private val viewModel = get<MqttConfigurationViewModel>()

    @Before
    fun setUp() {

        composeTestRule.setContent {
            TestContentProvider {
                MqttConfigurationScreen()
            }
        }

    }

    /**
     * MQTT disabled
     * switch is off
     * MQTT Settings not visible
     *
     * User clicks switch
     * mqtt enabled
     * switch is on
     * settings visible
     *
     * user click save
     * mqtt enabled saved
     */
    @Test
    fun testMqttContent() = runTest {
        viewModel.onEvent(SetMqttEnabled(false))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //MQTT disabled
        assertFalse { viewModel.viewState.value.editData.isMqttEnabled }
        //switch is off
        composeTestRule.onNodeWithTag(TestTag.MqttSwitch).onListItemSwitch().assertIsOff()
        //MQTT Settings not visible
        composeTestRule.onNodeWithTag(TestTag.Host).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.Port).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.UserName).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.Password).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.ConnectionTimeout).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.KeepAliveInterval).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTag.RetryInterval).assertDoesNotExist()

        //User clicks switch
        composeTestRule.onNodeWithTag(TestTag.MqttSwitch).performClick()
        //mqtt enabled
        composeTestRule.awaitIdle()
        assertTrue { viewModel.viewState.value.editData.isMqttEnabled }
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.MqttSwitch).onListItemSwitch().assertIsOn()
        //settings visible
        composeTestRule.onNodeWithTag(TestTag.Host).assertExists()
        composeTestRule.onNodeWithTag(TestTag.Port).assertExists()
        composeTestRule.onNodeWithTag(TestTag.UserName).assertExists()
        composeTestRule.onNodeWithTag(TestTag.Password).assertExists()
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).assertExists()
        composeTestRule.onNodeWithTag(TestTag.ConnectionTimeout).assertExists()
        composeTestRule.onNodeWithTag(TestTag.KeepAliveInterval).assertExists()
        composeTestRule.onNodeWithTag(TestTag.RetryInterval).assertExists()

        //user click save
        composeTestRule.saveBottomAppBar(viewModel)
        MqttConfigurationViewModel(get()).viewState.value.editData.also {
            //mqtt enabled saved
            assertEquals(true, it.isMqttEnabled)
        }
    }

    /**
     * mqtt is enabled
     * host is changed
     * port is changed
     * username is changed
     * password is changed
     *
     * user click save
     * host is saved
     * port is saved
     * username is saved
     * password is saved
     */
    @Test
    fun testMqttConnectionSettings() = runTest {
        viewModel.onEvent(SetMqttEnabled(true))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        val textInputTestHost = "hostTestInput"
        val textInputTestPort = "1672"
        val textInputTestUsername = "usernameTestInput"
        val textInputTestPassword = "passwordTestInput"

        //mqtt is enabled
        assertTrue { viewModel.viewState.value.editData.isMqttEnabled }
        //host is changed
        composeTestRule.onNodeWithTag(TestTag.Host).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Host).performTextClearance()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Host).performTextInput(textInputTestHost)
        composeTestRule.awaitIdle()
        //port is changed
        composeTestRule.onNodeWithTag(TestTag.Port).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Port).performTextClearance()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Port).performTextInput(textInputTestPort)
        composeTestRule.awaitIdle()
        //username is changed
        composeTestRule.onNodeWithTag(TestTag.UserName).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.UserName).performTextClearance()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.UserName).performTextInput(textInputTestUsername)
        composeTestRule.awaitIdle()
        //password is changed
        composeTestRule.onNodeWithTag(TestTag.Password).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Password).performTextClearance()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Password).performTextInput(textInputTestPassword)
        composeTestRule.awaitIdle()

        //user click save
        composeTestRule.saveBottomAppBar(viewModel)
        composeTestRule.awaitIdle()
        MqttConfigurationViewModel(get()).viewState.value.editData.also {
            //host is saved
            assertEquals(textInputTestHost, it.mqttHost)
            //port is saved
            assertEquals(textInputTestPort, it.mqttPortText)
            //username is saved
            assertEquals(textInputTestUsername, it.mqttUserName)
            //password is saved
            assertEquals(textInputTestPassword, it.mqttPassword)
        }
    }

    /**
     * mqtt is enabled
     * ssl is disabled
     * ssl is off
     *
     * user clicks ssl
     * ssl is enabled
     * ssl is on
     *
     * certificate button is shown
     *
     * user clicks save
     * ssl on is saved
     */
    @Test
    fun testMqttSSL() = runTest {
        viewModel.onEvent(SetMqttEnabled(true))
        viewModel.onEvent(SetMqttSSLEnabled(false))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //mqtt is enabled
        assertTrue { viewModel.viewState.value.editData.isMqttEnabled }
        //ssl is disabled
        assertFalse { viewModel.viewState.value.editData.isMqttSSLEnabled }
        //ssl is off
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOff()

        //user clicks ssl
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).performScrollTo().performClick()
        //ssl is enabled
        composeTestRule.awaitIdle()
        assertTrue { viewModel.viewState.value.editData.isMqttSSLEnabled }
        //ssl is on
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOn()

        //certificate button is shown
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertExists()

        //user clicks save
        composeTestRule.saveBottomAppBar(viewModel)
        MqttConfigurationViewModel(get()).viewState.value.editData.also {
            //ssl on is saved
            assertEquals(true, it.isMqttSSLEnabled)
        }
    }

    /**
     * mqtt is enabled
     * timeout is changed
     * keepAliveInterval is changed
     * retry interval is changed
     *
     * user click save
     * timeout is saved
     * keepAliveInterval is saved
     * retry interval is saved
     */
    @Test
    fun testMqttConnectionTiming() = runTest {
        viewModel.onEvent(SetMqttEnabled(true))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        val textInputTestConnectionTimeout = "498"
        val textInputTestKeepAliveInterval = "120"
        val textInputTestRetryInterval = "16504"

        //mqtt is enabled
        assertTrue { viewModel.viewState.value.editData.isMqttEnabled }
        //timeout is changed
        composeTestRule.onNodeWithTag(TestTag.ConnectionTimeout).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.ConnectionTimeout)
            .performTextReplacement(textInputTestConnectionTimeout)
        //keepAliveInterval is changed
        composeTestRule.onNodeWithTag(TestTag.KeepAliveInterval).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.KeepAliveInterval)
            .performTextReplacement(textInputTestKeepAliveInterval)
        //retry interval is changed
        composeTestRule.onNodeWithTag(TestTag.RetryInterval).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.RetryInterval)
            .performTextReplacement(textInputTestRetryInterval)

        //user click save
        composeTestRule.saveBottomAppBar(viewModel)
        MqttConfigurationViewModel(get()).viewState.value.editData.also {
            //timeout is saved
            assertEquals(textInputTestConnectionTimeout, it.mqttConnectionTimeoutText)
            //keepAliveInterval is saved
            assertEquals(textInputTestKeepAliveInterval, it.mqttKeepAliveIntervalText)
            //retry interval is saved
            assertEquals(textInputTestRetryInterval, it.mqttRetryIntervalText)
        }
    }

}