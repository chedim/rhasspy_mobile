package org.rhasspy.mobile.services.mqtt

/** Contains all possible MQTT states. */
@Suppress("unused")
enum class MqttStatus {
    SUCCESS,
    UNACCEPTABLE_PROTOCOL,
    IDENTIFIER_REJECTED,
    SERVER_UNAVAILABLE,
    INVALID_CREDENTIALS,
    NOT_AUTHORIZED,
    ALREADY_CONNECTED,
    MSG_DELIVERY_FAILED,
    MSG_PERSISTENCE_FAILED,
    SUBSCRIBE_FAILED,
    DISCONNECT_FAILED,
    UNSUBSCRIBE_FAILED,
    UNKNOWN
}
