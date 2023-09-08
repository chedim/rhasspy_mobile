package org.rhasspy.mobile.android.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import com.adevinta.android.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.*
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.ui.configuration.SpeechToTextConfigurationScreen
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiEvent.Action.Save
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationUiEvent.Change.SelectSpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewModel
import kotlin.test.assertEquals

class SpeechToTextConfigurationContentTest : FlakyTest() {

    private val viewModel = get<SpeechToTextConfigurationViewModel>()

    @Composable
    override fun ComposableContent() {
        SpeechToTextConfigurationScreen(viewModel)
    }

    /**
     * option disable is set
     * User clicks option remote http
     * new option is selected
     *
     * User clicks save
     * option is saved to remote http
     */
    @Test
    @AllowFlaky
    fun testEndpoint() = runTest {
        setupContent()

        viewModel.onEvent(SelectSpeechToTextOption(SpeechToTextOption.Disabled))
        viewModel.onEvent(Save)
        composeTestRule.awaitIdle()

        //option disable is set
        composeTestRule.onNodeWithTag(SpeechToTextOption.Disabled, true).onListItemRadioButton()
            .assertIsSelected()

        //User clicks option remote http
        composeTestRule.onNodeWithTag(SpeechToTextOption.RemoteHTTP).performClick()
        //new option is selected
        composeTestRule.awaitIdle()
        assertEquals(
            SpeechToTextOption.RemoteHTTP,
            viewModel.viewState.value.editData.speechToTextOption
        )

        //User clicks save
        composeTestRule.saveBottomAppBar()
        SpeechToTextConfigurationViewModel(get()).viewState.value.editData.also {
            //option is saved to remote http
            assertEquals(SpeechToTextOption.RemoteHTTP, it.speechToTextOption)
        }
    }

}