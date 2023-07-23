package org.rhasspy.mobile.android.settings.content

import android.widget.Switch
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.tooling.preview.org.rhasspy.mobile.ui.settings.IndicationSettingsContent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.rhasspy.mobile.android.utils.FlakyTest
import org.rhasspy.mobile.android.utils.TestContentProvider
import org.rhasspy.mobile.android.utils.onListItemRadioButton
import org.rhasspy.mobile.android.utils.onListItemSwitch
import org.rhasspy.mobile.android.utils.onNodeWithTag
import org.rhasspy.mobile.android.utils.resetOverlayPermission
import org.rhasspy.mobile.android.utils.text
import org.rhasspy.mobile.app.MainActivity
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.platformspecific.permission.IOverlayPermission
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.TestTag
import org.rhasspy.mobile.viewmodel.navigation.destinations.settings.IndicationSettingsScreenDestination
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetSoundIndicationEnabled
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetWakeWordDetectionTurnOnDisplay
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsUiEvent.Change.SetWakeWordLightIndicationEnabled
import org.rhasspy.mobile.viewmodel.settings.indication.IndicationSettingsViewModel
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IndicationSettingsContentTest : FlakyTest() {

    @get: Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val viewModel = get<IndicationSettingsViewModel>()

    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val settingsPage = "com.android.settings"
    private val list = ".*list"

    @Before
    fun setUp() {

        composeTestRule.activity.setContent {
            TestContentProvider {
                IndicationSettingsContent()
            }
        }

    }

    /**
     * wake up display disabled
     * visual disabled
     * sound disabled
     *
     * user clicks wake up display
     * wake up display is enabled
     * wake up display is saved
     *
     * user clicks visual
     * visual is enabled
     * visual is saved
     *
     * user clicks sound
     * sound is enabled
     * sound is saved
     */
    //@Test Manual
    @Suppress("unused")
    fun testIndicationSettings() {
        device.resetOverlayPermission(composeTestRule.activity, get())

        viewModel.onEvent(SetWakeWordLightIndicationEnabled(false))
        viewModel.onEvent(SetSoundIndicationEnabled(false))
        viewModel.onEvent(SetWakeWordDetectionTurnOnDisplay(false))

        //wake up display disabled
        composeTestRule.onNodeWithTag(TestTag.WakeWordDetectionTurnOnDisplay).onListItemSwitch()
            .assertIsOff()
        //visual disabled
        composeTestRule.onNodeWithTag(TestTag.WakeWordLightIndicationEnabled).onListItemSwitch()
            .assertIsOff()
        //sound disabled
        composeTestRule.onNodeWithTag(TestTag.SoundIndicationEnabled).onListItemSwitch()
            .assertIsOff()

        //user clicks wake up display
        composeTestRule.onNodeWithTag(TestTag.WakeWordDetectionTurnOnDisplay).performClick()
        composeTestRule.waitForIdle()
        //wake up display is enabled
        composeTestRule.onNodeWithTag(TestTag.WakeWordDetectionTurnOnDisplay).onListItemSwitch()
            .assertIsOn()
        //wake up display is saved
        assertTrue { IndicationSettingsViewModel(get()).viewState.value.isWakeWordDetectionTurnOnDisplayEnabled }


        //user clicks visual
        composeTestRule.onNodeWithTag(TestTag.WakeWordLightIndicationEnabled).performClick()
        //user accepts permission
        //Ok clicked
        composeTestRule.onNodeWithTag(TestTag.DialogOk).performClick()
        //on Q app is restarted when allowing overlay permission

        //Redirected to settings
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        device.wait(Until.hasObject(By.pkg(settingsPage.toPattern())), 5000)
        assertTrue { device.findObject(UiSelector().packageNameMatches(settingsPage)).exists() }
        UiScrollable(UiSelector().resourceIdMatches(list)).scrollIntoView(UiSelector().text(MR.strings.appName.stable))
        device.waitForIdle()

        device.findObject(UiSelector().text(MR.strings.appName.stable)).click()
        device.findObject(UiSelector().className(Switch::class.java)).click()
        //User clicks back
        device.pressBack()
        device.pressBack()

        //app will be closed when pressing one more back
        get<IOverlayPermission>().update()
        //Dialog is closed and permission granted
        assertTrue { get<IOverlayPermission>().granted.value }

        //visual is enabled
        composeTestRule.onNodeWithTag(TestTag.WakeWordLightIndicationEnabled).performClick()
        composeTestRule.onNodeWithTag(TestTag.WakeWordLightIndicationEnabled).onListItemSwitch()
            .assertIsOn()
        //visual is saved
        assertTrue { IndicationSettingsViewModel(get()).viewState.value.isWakeWordLightIndicationEnabled }

        //user clicks sound
        composeTestRule.onNodeWithTag(TestTag.SoundIndicationEnabled).performClick()
        //sound is enabled
        composeTestRule.onNodeWithTag(TestTag.SoundIndicationEnabled).onListItemSwitch()
            .assertIsOn()
        //sound is saved
        assertTrue { IndicationSettingsViewModel(get()).viewState.value.isSoundIndicationEnabled }
    }

    /**
     * Sound is disabled
     * sound settings invisible
     *
     * user clicks sound
     * sound is enabled
     * sound settings visible
     *
     * sound output is notification
     * sound output notification is selected
     *
     * user clicks sound output sound
     * sound output sound is selected
     * sound output sound is saved
     *
     * user clicks wake word
     * wake word page is opened
     * user clicks back
     *
     * user clicks recorded
     * recorded page is opened
     * user clicks back
     *
     * user click error
     * error page is opened
     * user clicks back
     */
    @Test
    fun testSoundIndicationOptions() = runTest {
        //Sound is disabled
        viewModel.onEvent(SetSoundIndicationEnabled(false))
        //sound settings invisible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertDoesNotExist()
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.WakeIndicationSoundScreen)
            .assertDoesNotExist()
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.RecordedIndicationSoundScreen)
            .assertDoesNotExist()
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.ErrorIndicationSoundScreen)
            .assertDoesNotExist()

        //user clicks sound
        composeTestRule.onNodeWithTag(TestTag.SoundIndicationEnabled).performClick()
        composeTestRule.awaitIdle()
        //sound is enabled
        composeTestRule.onNodeWithTag(TestTag.SoundIndicationEnabled).onListItemSwitch()
            .assertIsOn()
        //sound settings visible
        composeTestRule.onNodeWithTag(TestTag.AudioOutputOptions).assertIsDisplayed()
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.WakeIndicationSoundScreen)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.RecordedIndicationSoundScreen)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.ErrorIndicationSoundScreen)
            .assertIsDisplayed()

        //sound output is notification
        assertEquals(
            AudioOutputOption.Notification,
            viewModel.viewState.value.soundIndicationOutputOption
        )
        //sound output notification is selected
        composeTestRule.onNodeWithTag(AudioOutputOption.Notification, true).onListItemRadioButton()
            .assertIsSelected()

        //user clicks sound output sound
        composeTestRule.onNodeWithTag(AudioOutputOption.Sound).performClick()
        composeTestRule.awaitIdle()
        //sound output sound is selected
        composeTestRule.onNodeWithTag(AudioOutputOption.Sound, true).onListItemRadioButton()
            .assertIsSelected()
        //sound output sound is saved
        assertEquals(
            AudioOutputOption.Sound,
            IndicationSettingsViewModel(get()).viewState.value.soundIndicationOutputOption
        )

        //user clicks wake word
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.WakeIndicationSoundScreen)
            .performClick()
        //wake word page is opened
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.WakeIndicationSoundScreen)
            .assertIsDisplayed()
        //user clicks back
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()

        //user clicks recorded
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.RecordedIndicationSoundScreen)
            .performClick()
        //recorded page is opened
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.RecordedIndicationSoundScreen)
            .assertIsDisplayed()
        //user clicks back
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()

        //user click error
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.ErrorIndicationSoundScreen)
            .performClick()
        //error page is opened
        composeTestRule.onNodeWithTag(IndicationSettingsScreenDestination.ErrorIndicationSoundScreen)
            .assertIsDisplayed()
        //user clicks back
        composeTestRule.onNodeWithTag(TestTag.AppBarBackButton).performClick()
    }

}