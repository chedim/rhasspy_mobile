package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.awaitSaved
import org.rhasspy.mobile.android.main.LocalMainNavController
import org.rhasspy.mobile.android.onListItemSwitch
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.SetMqttEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationUiEvent.Change.SetMqttSSLEnabled
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.mqtt.MqttConfigurationViewState
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MqttConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = MqttConfigurationViewModel(
        service = MqttService(),
        testRunner = MqttConfigurationTest()
    )

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                MqttConfigurationContent(viewModel)
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
    fun testMqttContent() = runBlocking {
        viewModel.onAction(SetMqttEnabled(false))
        viewModel.onSave()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        //MQTT disabled
        assertFalse { viewState.value.isMqttEnabled }
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
        assertTrue { viewState.value.isMqttEnabled }
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
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewState = MqttConfigurationViewState()
        //mqtt enabled saved
        assertEquals(true, newViewState.isMqttEnabled)
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
    fun testMqttConnectionSettings() = runBlocking {
        viewModel.onAction(SetMqttEnabled(true))
        viewModel.onSave()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTestHost = "hostTestInput"
        val textInputTestPort = "1672"
        val textInputTestUsername = "usernameTestInput"
        val textInputTestPassword = "passwordTestInput"

        //mqtt is enabled
        assertTrue { viewState.value.isMqttEnabled }
        //host is changed
        composeTestRule.onNodeWithTag(TestTag.Host).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Host).performTextReplacement(textInputTestHost)
        //port is changed
        composeTestRule.onNodeWithTag(TestTag.Port).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Port).performTextReplacement(textInputTestPort)
        //username is changed
        composeTestRule.onNodeWithTag(TestTag.UserName).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.UserName)
            .performTextReplacement(textInputTestUsername)
        //password is changed
        composeTestRule.onNodeWithTag(TestTag.Password).performScrollTo().performClick()
        composeTestRule.awaitIdle()
        composeTestRule.onNodeWithTag(TestTag.Password)
            .performTextReplacement(textInputTestPassword)

        //user click save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewState = MqttConfigurationViewState()
        //host is saved
        assertEquals(textInputTestHost, newViewState.mqttHost)
        //port is saved
        assertEquals(textInputTestPort, newViewState.mqttPortText)
        //username is saved
        assertEquals(textInputTestUsername, newViewState.mqttUserName)
        //password is saved
        assertEquals(textInputTestPassword, newViewState.mqttPassword)
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
    fun testMqttSSL() = runBlocking {
        viewModel.onAction(SetMqttEnabled(true))
        viewModel.onAction(SetMqttSSLEnabled(false))
        viewModel.onSave()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        //mqtt is enabled
        assertTrue { viewState.value.isMqttEnabled }
        //ssl is disabled
        assertFalse { viewState.value.isMqttSSLEnabled }
        //ssl is off
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOff()

        //user clicks ssl
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).performScrollTo().performClick()
        //ssl is enabled
        assertTrue { viewState.value.isMqttSSLEnabled }
        //ssl is on
        composeTestRule.onNodeWithTag(TestTag.SSLSwitch).onListItemSwitch().assertIsOn()

        //certificate button is shown
        composeTestRule.onNodeWithTag(TestTag.CertificateButton).assertExists()

        //user clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val newViewState = MqttConfigurationViewState()
        //ssl on is saved
        assertEquals(true, newViewState.isMqttSSLEnabled)
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
    fun testMqttConnectionTiming() = runBlocking {
        viewModel.onAction(SetMqttEnabled(true))
        viewModel.onSave()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTestConnectionTimeout = "498"
        val textInputTestKeepAliveInterval = "120"
        val textInputTestRetryInterval = "16504"

        //mqtt is enabled
        assertTrue { viewState.value.isMqttEnabled }
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
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val newViewState = MqttConfigurationViewState()
        //timeout is saved
        assertEquals(textInputTestConnectionTimeout, newViewState.mqttConnectionTimeoutText)
        //keepAliveInterval is saved
        assertEquals(textInputTestKeepAliveInterval, newViewState.mqttKeepAliveIntervalText)
        //retry interval is saved
        assertEquals(textInputTestRetryInterval, newViewState.mqttRetryIntervalText)
    }

}