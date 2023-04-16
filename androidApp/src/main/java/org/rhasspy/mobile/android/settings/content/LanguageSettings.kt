package org.rhasspy.mobile.android.settings.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.get
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.content.elements.RadioButtonsEnumSelection
import org.rhasspy.mobile.android.settings.SettingsScreenItemContent
import org.rhasspy.mobile.android.settings.SettingsScreenType
import org.rhasspy.mobile.android.testTag
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsUiEvent.Change.SetLanguageOption
import org.rhasspy.mobile.viewmodel.settings.language.LanguageSettingsViewModel

/**
 * language setting screen
 * radio button list with different languages
 */
@Preview
@Composable
fun LanguageSettingsScreenItemContent(viewModel: LanguageSettingsViewModel = get()) {

    val viewState by viewModel.viewState.collectAsState()

    SettingsScreenItemContent(
        modifier = Modifier.testTag(SettingsScreenType.LanguageSettings),
        title = MR.strings.language.stable
    ) {

        RadioButtonsEnumSelection(
            selected = viewState.languageOption,
            onSelect = { viewModel.onEvent(SetLanguageOption(it)) },
            values = viewState.languageOptions
        )

    }

}