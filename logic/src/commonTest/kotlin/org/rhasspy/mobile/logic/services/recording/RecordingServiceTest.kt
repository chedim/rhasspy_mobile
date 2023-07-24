package org.rhasspy.mobile.logic.services.recording

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.koin.core.component.get
import org.koin.dsl.module
import org.rhasspy.mobile.logic.AppTest
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.SilenceDetected
import org.rhasspy.mobile.logic.nVerify
import org.rhasspy.mobile.platformspecific.audiorecorder.IAudioRecorder
import org.rhasspy.mobile.settings.AppSetting
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds


class RecordingServiceTest : AppTest() {

    private val audioRecorder = FakeAudioRecorder()
    private val threshold = 12767.5f
    private val allowedDelay = 100.milliseconds //delay allowed until silence is detected
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @Mock
    lateinit var serviceMiddleware: IServiceMiddleware

    override fun setUpMocks() = injectMocks(mocker)

    @BeforeTest
    fun before() {
        super.before(
            module {
                single<IAudioRecorder> { audioRecorder }
                single<IServiceMiddleware> { serviceMiddleware }
            }
        )

        //enable silence detection
        AppSetting.isAutomaticSilenceDetectionEnabled.value = true
        //set threshold, must be below may volume
        AppSetting.automaticSilenceDetectionAudioLevel.value = threshold
    }

    @Test
    fun `when minimum duration is set silence detection is only triggered after the time even though volume is below threshold`() =
        runTest {
            every { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } returns Unit

            val recordingService = get<IRecordingService>()

            //setup minimum time for recording
            AppSetting.automaticSilenceDetectionMinimumTime.value = 500 //ms
            AppSetting.automaticSilenceDetectionTime.value = 0 //ms

            //sent varying (random) data below threshold
            val job = coroutineScope.launch {
                while (true) {
                    audioRecorder.sendMaxVolume(Random.nextFloat() * (threshold - 1))
                    delay(10)
                }
            }

            coroutineScope.launch {
                recordingService.output.collect()
            }
            recordingService.toggleSilenceDetectionEnabled(true)

            val job2 = coroutineScope.launch {
                var time = 0
                while (time < 500) {
                    nVerify { repeat(0) { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } }
                    delay(10)
                    time += 10
                }
                delay(allowedDelay)
                nVerify { repeat(1) { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } }
            }
            //check that silence detection is triggered after set minimum time for recording
            joinAll(job2)
            job.cancel()
        }

    @Test
    fun `when silence detection time is set the detection is only triggered when it is silent for a specific amount of time`() =
        runTest {
            every { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } returns Unit

            val recordingService = get<IRecordingService>()

            //setup silence detection time
            AppSetting.automaticSilenceDetectionTime.value = 200 //ms
            //setup minimum time for recording
            AppSetting.automaticSilenceDetectionMinimumTime.value = 0 //ms

            //sent varying (random) data below threshold
            val job = coroutineScope.launch {
                while (true) {
                    audioRecorder.sendMaxVolume(Random.nextFloat() * (threshold - 1))
                    delay(10)
                }
            }

            coroutineScope.launch {
                recordingService.output.collect()
            }
            recordingService.toggleSilenceDetectionEnabled(true)

            val job2 = coroutineScope.launch {
                var time = 0
                while (time < 200) {
                    nVerify { repeat(0) { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } }
                    delay(10)
                    time += 10
                }
                delay(allowedDelay)
                nVerify { repeat(1) { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } }
            }
            //check that silence detection is triggered after set minimum time for recording
            joinAll(job2)
            job.cancel()
        }

    @Test
    fun `when silence detection time and minimum duration work together`() = runTest {
        every { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } returns Unit

        val recordingService = get<IRecordingService>()

        //setup silence detection time
        AppSetting.automaticSilenceDetectionTime.value = 200 //ms
        //setup minimum time for recording
        AppSetting.automaticSilenceDetectionMinimumTime.value = 500 //ms

        //sent varying (random) data below threshold
        val job = coroutineScope.launch {
            while (true) {
                audioRecorder.sendMaxVolume(Random.nextFloat() * (threshold - 1))
                delay(10)
            }
        }

        coroutineScope.launch {
            recordingService.output.collect()
        }
        recordingService.toggleSilenceDetectionEnabled(true)

        val job2 = coroutineScope.launch {
            var time = 0
            //check that silence detection is triggered after silence detection time + setup minimum time for recording combined
            while (time < (200 + 500)) {
                nVerify { repeat(0) { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } }
                delay(10)
                time += 10
            }
            delay(allowedDelay)
            nVerify { repeat(1) { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } }
        }
        //check that silence detection is triggered after set minimum time for recording
        joinAll(job2)
        job.cancel()
    }

    @Test
    fun `when silence detection time is set the detection is not triggered when volume increases above threshold after being below it for not the full silence detection time`() =
        runBlocking {
            every { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } returns Unit

            val recordingService = get<IRecordingService>()

            //setup silence detection time
            AppSetting.automaticSilenceDetectionTime.value = 200 //ms
            //setup minimum time for recording
            AppSetting.automaticSilenceDetectionMinimumTime.value = 0 //ms

            //sent varying (random) data below threshold
            val job = coroutineScope.launch {
                var time = 0
                //sent varying (random) data below threshold for half the silence detection time
                while (true) {
                    val volume =
                        if (time < 200) Random.nextFloat() * (threshold - 1) else Random.nextFloat() * (32767.0f - threshold) + (threshold + 1)
                    audioRecorder.sendMaxVolume(volume)
                    delay(10)
                    time += 10
                }
            }

            coroutineScope.launch {
                recordingService.output.collect()
            }
            recordingService.toggleSilenceDetectionEnabled(true)

            val job2 = coroutineScope.launch {
                var time = 0
                //check that silence detection is not triggered
                while (time < (500)) {
                    nVerify { repeat(0) { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } }
                    delay(allowedDelay)
                    time += 10
                }
            }
            //check that silence detection is triggered after set minimum time for recording
            joinAll(job2)
            job.cancel()
        }

    @Test
    fun `when neither minimum duration nor silence detection time is set the detection is instantly triggered when a value falls below threshold`() =
        runBlocking {
            every { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } returns Unit

            val recordingService = get<IRecordingService>()

            //setup silence detection time to 0
            AppSetting.automaticSilenceDetectionTime.value = 0 //ms
            //setup minimum time for recording to 0
            AppSetting.automaticSilenceDetectionMinimumTime.value = 0 //ms

            val job = coroutineScope.launch {
                //sent varying (random) data below threshold
                while (true) {
                    audioRecorder.sendMaxVolume(Random.nextFloat() * (threshold - 1))
                    delay(10)
                }
            }

            coroutineScope.launch {
                recordingService.output.collect()
            }
            recordingService.toggleSilenceDetectionEnabled(true)

            delay(allowedDelay)

            //check that silence detection is triggered instant
            nVerify { repeat(1) { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } }

            job.cancel()
        }

    @Test
    fun `when volume stays above threshold silence detection is not triggered`() = runBlocking {
        every { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } returns Unit

        val recordingService = get<IRecordingService>()

        //setup silence detection time
        AppSetting.automaticSilenceDetectionTime.value = 200 //ms
        //setup minimum time for recording
        AppSetting.automaticSilenceDetectionMinimumTime.value = 500 //ms

        //sent varying (random) data above threshold
        val job = coroutineScope.launch {
            //sent varying (random) data below threshold for half the silence detection time
            while (true) {
                audioRecorder.sendMaxVolume(Random.nextFloat() * (threshold + 1))
                delay(10)
            }
        }

        coroutineScope.launch {
            recordingService.output.collect()
        }
        recordingService.toggleSilenceDetectionEnabled(true)

        delay(1000)

        //check that silence detection is not triggered
        nVerify { repeat(0) { serviceMiddleware.action(isInstanceOf<SilenceDetected>()) } }

        job.cancel()
    }

}
