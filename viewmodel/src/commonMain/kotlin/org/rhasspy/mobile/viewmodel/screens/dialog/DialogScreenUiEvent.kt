package org.rhasspy.mobile.viewmodel.screens.dialog

sealed interface DialogScreenUiEvent {

    sealed interface Change : DialogScreenUiEvent {

        data object ToggleListAutoScroll : Change

    }

}