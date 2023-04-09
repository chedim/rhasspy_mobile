package org.rhasspy.mobile.android.configuration.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreenType
import org.rhasspy.mobile.android.content.list.FilledTonalButtonListItem
import org.rhasspy.mobile.android.content.list.SwitchListItem
import org.rhasspy.mobile.android.content.list.TextFieldListItem
import org.rhasspy.mobile.android.permissions.RequiresMicrophonePermission
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.configuration.RemoteHermesHttpConfigurationViewModel

/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Preview
@Composable
fun RemoteHermesHttpConfigurationContent(viewModel: RemoteHermesHttpConfigurationViewModel = get()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreenType.RemoteHermesHttpConfiguration),
        title = MR.strings.remoteHermesHTTP.stable,
        viewState = viewModel.viewState.collectAsState().value,
        onAction = viewModel::onAction,
        testContent = { TestContent(viewModel) }
    ) {

        item {
            //base http endpoint
            TextFieldListItem(
                label = MR.strings.baseHost.stable,
                modifier = Modifier.testTag(TestTag.Host),
                value = viewModel.httpClientServerEndpointHost.collectAsState().value,
                onValueChange = viewModel::updateHttpClientServerEndpointHost,
                isLastItem = false
            )
        }

        item {
            //port
            TextFieldListItem(
                label = MR.strings.port.stable,
                modifier = Modifier.testTag(TestTag.Port),
                value = viewModel.httpClientServerEndpointPort.collectAsState().value,
                onValueChange = viewModel::updateHttpClientServerEndpointPort,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }

        item {
            //timeout
            TextFieldListItem(
                label = MR.strings.requestTimeout.stable,
                modifier = Modifier.testTag(TestTag.Timeout),
                value = viewModel.httpClientTimeoutText.collectAsState().value,
                onValueChange = viewModel::updateHttpClientTimeout,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }

        item {
            //switch to toggle validation of SSL certificate
            SwitchListItem(
                text = MR.strings.disableSSLValidation.stable,
                modifier = Modifier.testTag(TestTag.SSLSwitch),
                secondaryText = MR.strings.disableSSLValidationInformation.stable,
                isChecked = viewModel.isHttpSSLVerificationDisabled.collectAsState().value,
                onCheckedChange = viewModel::toggleHttpSSLVerificationDisabled
            )
        }

    }

}

/**
 * test http connection button
 */
@Composable
private fun TestContent(viewModel: RemoteHermesHttpConfigurationViewModel) {

    Column {

        if (viewModel.isSpeechToTextTestVisible.collectAsState().value) {
            RequiresMicrophonePermission(
                MR.strings.microphonePermissionInfoRecord.stable,
                viewModel::toggleRecording
            ) { onClick ->
                FilledTonalButtonListItem(
                    text = if (viewModel.isRecordingAudio.collectAsState().value) MR.strings.stopRecordAudio.stable else MR.strings.startRecordAudio.stable,
                    onClick = onClick
                )
            }
        }

        if (viewModel.isIntentRecognitionTestVisible.collectAsState().value) {
            TextFieldListItem(
                label = MR.strings.textIntentRecognitionText.stable,
                modifier = Modifier.testTag(TestTag.TextToSpeechText),
                value = viewModel.testIntentRecognitionText.collectAsState().value,
                onValueChange = viewModel::updateTestIntentRecognitionText
            )

            FilledTonalButtonListItem(
                text = MR.strings.executeIntentRecognition.stable,
                enabled = viewModel.isIntentRecognitionTestEnabled.collectAsState().value,
                onClick = viewModel::runIntentRecognitionTest
            )
        }

        if (viewModel.isTextToSpeechTestVisible.collectAsState().value) {
            TextFieldListItem(
                label = MR.strings.textToSpeechText.stable,
                modifier = Modifier.testTag(TestTag.TextToSpeechText),
                value = viewModel.testTextToSpeechText.collectAsState().value,
                onValueChange = viewModel::updateTestTextToSpeechText
            )

            FilledTonalButtonListItem(
                text = MR.strings.executeTextToSpeechText.stable,
                enabled = viewModel.isTextToSpeechTestEnabled.collectAsState().value,
                onClick = viewModel::runTextToSpeechTest
            )
        }
    }

}