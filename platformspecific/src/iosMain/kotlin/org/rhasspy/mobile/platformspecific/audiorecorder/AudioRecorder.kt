package org.rhasspy.mobile.platformspecific.audiorecorder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType

actual class AudioRecorder {

    /**
     * max volume since start recording
     */
    actual val maxVolume: StateFlow<Short>
        get() = TODO("Not yet implemented")
    actual val isRecording: StateFlow<Boolean>
        get() = TODO("Not yet implemented")
    actual val absoluteMaxVolume: Double
        get() = TODO("Not yet implemented")

    /**
     * start recording
     */
    actual fun startRecording(
        audioRecorderSampleRateType: AudioRecorderSampleRateType,
        audioRecorderChannelType: AudioRecorderChannelType,
        audioRecorderEncodingType: AudioRecorderEncodingType
    ) {
    }

    /**
     * stop recording
     */
    actual fun stopRecording() {
    }

    /**
     * output data as flow
     */
    actual val output: Flow<ByteArray>
        get() = TODO("Not yet implemented")

}