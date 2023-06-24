package org.rhasspy.mobile.viewmodel.navigation.destinations

import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

sealed interface ConfigurationScreenNavigationDestination : NavigationDestination {

    object AudioPlayingConfigurationScreen : ConfigurationScreenNavigationDestination
    object DialogManagementConfigurationScreen : ConfigurationScreenNavigationDestination
    object IntentHandlingConfigurationScreen : ConfigurationScreenNavigationDestination
    object IntentRecognitionConfigurationScreen : ConfigurationScreenNavigationDestination
    object MqttConfigurationScreen : ConfigurationScreenNavigationDestination
    object RemoteHermesHttpConfigurationScreen : ConfigurationScreenNavigationDestination
    object SpeechToTextConfigurationScreen : ConfigurationScreenNavigationDestination
    object TextToSpeechConfigurationScreen : ConfigurationScreenNavigationDestination
    object WakeWordConfigurationScreen : ConfigurationScreenNavigationDestination
    object WebServerConfigurationScreen : ConfigurationScreenNavigationDestination

}