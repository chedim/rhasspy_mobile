package org.rhasspy.mobile.android.configuration.content

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
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
import org.rhasspy.mobile.android.onListItemRadioButton
import org.rhasspy.mobile.android.onListItemSwitch
import org.rhasspy.mobile.android.onNodeWithTag
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiAction.Change.SelectTextToSpeechOption
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewState
import kotlin.test.assertEquals

class TextToSpeechConfigurationContentTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private val viewModel = TextToSpeechConfigurationViewModel(
        service = TextToSpeechService(),
        testRunner = TextToSpeechConfigurationTest()
    )

    @Before
    fun setUp() {

        composeTestRule.setContent {
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalMainNavController provides navController
            ) {
                TextToSpeechConfigurationContent(viewModel)
            }
        }

    }

    /**
     * option disable is set
     * User clicks option remote http
     * new option is selected
     *
     * Endpoint visible
     * custom endpoint switch visible
     *
     * switch is off
     * endpoint cannot be changed
     *
     * user clicks switch
     * switch is on
     * endpoint can be changed
     *
     * User clicks save
     * option is saved to remote http
     * endpoint is saved
     * use custom endpoint is saved
     */
    @Test
    fun testEndpoint() = runBlocking {
        viewModel.onAction(SelectTextToSpeechOption(TextToSpeechOption.Disabled))
        viewModel.save()
        composeTestRule.awaitSaved(viewModel)
        composeTestRule.awaitIdle()
        val viewState = viewModel.viewState.value.editViewState

        val textInputTest = "endpointTestInput"

        //option disable is set
        composeTestRule.onNodeWithTag(TextToSpeechOption.Disabled, true).onListItemRadioButton().assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(TextToSpeechOption.RemoteHTTP).performClick()
        //new option is selected
        assertEquals(TextToSpeechOption.RemoteHTTP, viewState.value.textToSpeechOption)

        //Endpoint visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()
        //custom endpoint switch visible
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertExists()

        //switch is off
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performScrollTo().onListItemSwitch().assertIsOff()
        //endpoint cannot be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsNotEnabled()

        //user clicks switch
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).performClick()
        //switch is on
        composeTestRule.onNodeWithTag(TestTag.CustomEndpointSwitch).onListItemSwitch().assertIsOn()
        //endpoint can be changed
        composeTestRule.onNodeWithTag(TestTag.Endpoint).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTag.Endpoint).performTextReplacement(textInputTest)
        composeTestRule.awaitIdle()
        assertEquals(textInputTest, viewState.value.textToSpeechHttpEndpoint)

        //User clicks save
        composeTestRule.onNodeWithTag(TestTag.BottomAppBarSave).assertIsEnabled().performClick()
        composeTestRule.awaitSaved(viewModel)
        val newViewState = TextToSpeechConfigurationViewState()
        //option is saved to remote http
        assertEquals(TextToSpeechOption.RemoteHTTP, newViewState.textToSpeechOption)
        //endpoint is saved
        assertEquals(textInputTest, newViewState.textToSpeechHttpEndpoint)
        //use custom endpoint is saved
        assertEquals(true, newViewState.isUseCustomTextToSpeechHttpEndpoint)
    }

}