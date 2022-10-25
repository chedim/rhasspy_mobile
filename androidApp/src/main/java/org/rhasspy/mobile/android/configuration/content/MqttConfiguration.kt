package org.rhasspy.mobile.android.configuration.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.TestTag
import org.rhasspy.mobile.android.configuration.ConfigurationScreenItemContent
import org.rhasspy.mobile.android.configuration.ConfigurationScreens
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.android.utils.OutlineButtonListItem
import org.rhasspy.mobile.android.utils.SwitchListItem
import org.rhasspy.mobile.android.utils.TextFieldListItem
import org.rhasspy.mobile.android.utils.TextFieldListItemVisibility
import org.rhasspy.mobile.viewModels.configuration.MqttConfigurationViewModel


/**
 * mqtt configuration content
 * Enable/Disable switch
 * when enabled then
 * connection settings (host, port)
 * option to test connection
 * ssl settings
 * connection timeout settings
 */
@Preview
@Composable
fun MqttConfigurationContent(viewModel: MqttConfigurationViewModel = viewModel()) {

    ConfigurationScreenItemContent(
        modifier = Modifier.testTag(ConfigurationScreens.MqttConfiguration),
        title = MR.strings.mqtt,
        hasUnsavedChanges = viewModel.hasUnsavedChanges,
        onSave = viewModel::save,
        onTest = viewModel::test,
        onDiscard = viewModel::discard
    ) {

        //toggle to turn mqtt enabled on or off
        SwitchListItem(
            text = MR.strings.externalMQTT,
            modifier = Modifier.testTag(TestTag.MqttSwitch),
            isChecked = viewModel.isMqttEnabled.collectAsState().value,
            onCheckedChange = viewModel::toggleMqttEnabled
        )

        //visibility of mqtt settings
        AnimatedVisibility(
            enter = expandVertically(),
            exit = shrinkVertically(),
            visible = viewModel.isMqttSettingsVisible.collectAsState().value
        ) {

            Column {

                MqttConnectionSettings(viewModel)

                MqttSSL(viewModel)

                MqttConnectionTiming(viewModel)

            }

        }

    }

}

/**
 * connection settings for mqtt
 * text fields for
 * host, port, username, password
 */
@Composable
private fun MqttConnectionSettings(viewModel: MqttConfigurationViewModel) {

    //host
    TextFieldListItem(
        label = MR.strings.host,
        modifier = Modifier.testTag(TestTag.Host),
        value = viewModel.mqttHost.collectAsState().value,
        onValueChange = viewModel::updateMqttHost,
    )

    //port
    TextFieldListItem(
        label = MR.strings.port,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.testTag(TestTag.Port),
        value = viewModel.mqttPort.collectAsState().value,
        onValueChange = viewModel::updateMqttPort,
    )

    //username
    TextFieldListItem(
        value = viewModel.mqttUserName.collectAsState().value,
        onValueChange = viewModel::updateMqttUserName,
        modifier = Modifier.testTag(TestTag.UserName),
        label = MR.strings.userName
    )

    //password
    TextFieldListItemVisibility(
        value = viewModel.mqttPassword.collectAsState().value,
        onValueChange = viewModel::updateMqttPassword,
        modifier = Modifier.testTag(TestTag.Password),
        label = MR.strings.password
    )
}

/**
 * switch to enable mqtt ssl
 * button to select certificate
 */
@Composable
private fun MqttSSL(viewModel: MqttConfigurationViewModel) {

    SwitchListItem(
        text = MR.strings.enableSSL,
        modifier = Modifier.testTag(TestTag.SSLSwitch),
        isChecked = viewModel.isMqttSSLEnabled.collectAsState().value,
        onCheckedChange = viewModel::toggleMqttSSLEnabled
    )

    AnimatedVisibility(
        enter = expandVertically(),
        exit = shrinkVertically(),
        visible = viewModel.isMqttChooseCertificateVisible.collectAsState().value
    ) {

        OutlineButtonListItem(
            text = MR.strings.chooseCertificate,
            modifier = Modifier.testTag(TestTag.CertificateButton),
            onClick = { })

    }

}

/**
 * Time settings for mqtt connection
 * timeout, keepAliveInterval, retryInterval
 */
@Composable
private fun MqttConnectionTiming(viewModel: MqttConfigurationViewModel) {

    TextFieldListItem(
        label = MR.strings.connectionTimeout,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.testTag(TestTag.ConnectionTimeout),
        value = viewModel.mqttConnectionTimeout.collectAsState().value,
        onValueChange = viewModel::updateMqttConnectionTimeout
    )

    TextFieldListItem(
        label = MR.strings.keepAliveInterval,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.testTag(TestTag.KeepAliveInterval),
        value = viewModel.mqttKeepAliveInterval.collectAsState().value,
        onValueChange = viewModel::updateMqttKeepAliveInterval
    )

    TextFieldListItem(
        label = MR.strings.retryInterval,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.testTag(TestTag.RetryInterval),
        value = viewModel.mqttRetryInterval.collectAsState().value,
        onValueChange = viewModel::updateMqttRetryInterval
    )

}