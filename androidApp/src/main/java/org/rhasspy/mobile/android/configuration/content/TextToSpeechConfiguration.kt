package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.theme.CardPaddingLevel1
import org.rhasspy.mobile.android.utils.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.viewModels.configuration.TextToSpeechConfigurationViewModel


/**
 * Content to configure text to speech
 * Drop Down of state
 * HTTP Endpoint
 */
@Preview
@Composable
fun TextToSpeechConfigurationContent(viewModel: TextToSpeechConfigurationViewModel = viewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.TextToSpeechConfiguration),
        title = MR.strings.textToSpeech,
        hasUnsavedChanges = viewModel.hasUnsavedChanges,
        onSave = viewModel::save,
        onTest = viewModel::test,
        onDiscard = viewModel::discard
    ) {

        //drop down to select text to speech
        RadioButtonsEnumSelection(
            modifier = Modifier.testTag(TestTag.TextToSpeechOptions),
            selected = viewModel.textToSpeechOption.collectAsState().value,
            onSelect = viewModel::selectTextToSpeechOption,
            values = viewModel.textToSpeechOptions
        ) {

            if (viewModel.isTextToSpeechHttpSettingsVisible(it)) {
                Card(
                    modifier = Modifier.padding(CardPaddingLevel1),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    TextToSpeechHTTP(viewModel)
                }
            }

        }

    }

}

@Composable
private fun TextToSpeechHTTP(viewModel: TextToSpeechConfigurationViewModel) {

    //switch to use custom
    SwitchListItem(
        modifier = Modifier.testTag(TestTag.CustomEndpointSwitch),
        text = MR.strings.useCustomEndpoint,
        isChecked = viewModel.isUseCustomTextToSpeechHttpEndpoint.collectAsState().value,
        onCheckedChange = viewModel::toggleUseCustomHttpEndpoint
    )

    //http endpoint input field
    TextFieldListItem(
        enabled = viewModel.isTextToSpeechHttpEndpointChangeEnabled.collectAsState().value,
        modifier = Modifier
            .testTag(TestTag.Endpoint),
        value = viewModel.textToSpeechHttpEndpoint.collectAsState().value,
        onValueChange = viewModel::updateTextToSpeechHttpEndpoint,
        label = MR.strings.rhasspyTextToSpeechURL
    )

}
