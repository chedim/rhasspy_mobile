package org.rhasspy.mobile.ui.configuration.connection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.rhasspy.mobile.data.connection.HttpConnection
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR
import org.rhasspy.mobile.ui.content.LocalViewModelFactory
import org.rhasspy.mobile.ui.content.Screen
import org.rhasspy.mobile.ui.content.elements.Icon
import org.rhasspy.mobile.ui.content.elements.toText
import org.rhasspy.mobile.ui.content.elements.translate
import org.rhasspy.mobile.ui.content.list.ListElement
import org.rhasspy.mobile.viewmodel.configuration.connections.http.list.HttpConnectionListConfigurationUiEvent
import org.rhasspy.mobile.viewmodel.configuration.connections.http.list.HttpConnectionListConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.configuration.connections.http.list.HttpConnectionListConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.connections.http.list.HttpConnectionListConfigurationViewState

/**
 * content to configure http configuration
 * switch to disable ssl verification
 */
@Composable
fun HttpConnectionListScreen() {

    val viewModel: HttpConnectionListConfigurationViewModel = LocalViewModelFactory.current.getViewModel() //TODO list view model

    Screen(
        screenViewModel = viewModel,
        title = MR.strings.remote_http.stable,
        onBackClick = { viewModel.onEvent(BackClick) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(AddClick) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = MR.strings.add.stable
                )
            }
        }
    ) {

        val viewState by viewModel.viewState.collectAsState()

        HttpConnectionListContent(
            viewState = viewState,
            onEvent = viewModel::onEvent
        )

    }

}

@Composable
private fun HttpConnectionListContent(
    viewState: HttpConnectionListConfigurationViewState,
    onEvent: (HttpConnectionListConfigurationUiEvent) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {

        items(viewState.items) {
            HttpConnectionListContent(
                viewState = it.connection,
                onEvent = onEvent
            )
            Divider()
        }

    }

}

@Composable
private fun HttpConnectionListContent(
    viewState: HttpConnection,
    onEvent: (HttpConnectionListConfigurationUiEvent) -> Unit
) {

    ListElement(
        modifier = Modifier.clickable { onEvent(ItemClick(viewState.id)) },
        text = { Text("${viewState.host}:${viewState.port}") },
        secondaryText = {
            Column {
                Text("isRhasspy2Hermes: ${translate(viewState.isHermes.toText())}")
                Text("isRhasspy3Wyoming: ${translate(viewState.isWyoming.toText())}")
                Text("isHomeAssistant: ${translate(viewState.isHomeAssistant.toText())}")
            }
        },
        overlineText = { Text("${viewState.host}:${viewState.port}") },
        trailing = {
            IconButton(
                onClick = { onEvent(ItemDeleteClick(viewState.id)) }
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = MR.strings.delete.stable
                )
            }
        }
    )

}