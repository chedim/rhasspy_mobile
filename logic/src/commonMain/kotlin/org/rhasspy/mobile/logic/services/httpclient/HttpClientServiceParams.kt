package org.rhasspy.mobile.logic.services.httpclient

import org.rhasspy.mobile.data.service.option.IntentHandlingOption

data class HttpClientServiceParams(
    val siteId: String,
    val isHttpSSLVerificationDisabled: Boolean,
    val httpClientServerEndpointHost: String,
    val httpClientServerEndpointPort: Int,
    val httpClientTimeout: Long?,
    val isUseCustomSpeechToTextHttpEndpoint: Boolean,
    val speechToTextHttpEndpoint: String,
    val isUseCustomIntentRecognitionHttpEndpoint: Boolean,
    val intentRecognitionHttpEndpoint: String,
    val isUseCustomTextToSpeechHttpEndpoint: Boolean,
    val textToSpeechHttpEndpoint: String,
    val isUseCustomAudioPlayingEndpoint: Boolean,
    val audioPlayingHttpEndpoint: String,
    val intentHandlingHttpEndpoint: String,
    val intentHandlingHassEndpoint: String,
    val intentHandlingHassAccessToken: String,
    val intentHandlingOption: IntentHandlingOption
)