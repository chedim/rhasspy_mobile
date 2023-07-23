package org.rhasspy.mobile.logic.services.dialog

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.DialogManagementOption.Disabled
import org.rhasspy.mobile.data.service.option.DialogManagementOption.Local
import org.rhasspy.mobile.data.service.option.DialogManagementOption.RemoteMQTT
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrTextCaptured
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.IntentRecognitionError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayFinished
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.SessionEnded
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.SessionStarted
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.WakeWordDetected
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.IdleState
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.DialogManagerDisabled
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.DialogManagerLocal
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.DialogManagerRemoteMqtt
import org.rhasspy.mobile.logic.services.dialog.states.IStateTransition
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.platformspecific.updateList
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IDialogManagerService : IService {

    val dialogHistory: StateFlow<List<Pair<DialogServiceMiddlewareAction, DialogManagerState>>>

    override val serviceState: StateFlow<ServiceState>
    val currentDialogState: StateFlow<DialogManagerState>

    fun start()
    fun transitionTo(action: DialogServiceMiddlewareAction, state: DialogManagerState)

    fun onAction(action: DialogServiceMiddlewareAction)

    suspend fun informMqtt(sessionData: SessionData?, action: DialogServiceMiddlewareAction)
}

/**
 * The Dialog Manager handles the various states and goes to the next state according to the function that is called
 */
internal class DialogManagerService(
    private val mqttService: IMqttService
) : IDialogManagerService {

    private val dialogManagerLocal by inject<DialogManagerLocal>()
    private val dialogManagerRemoteMqtt by inject<DialogManagerRemoteMqtt>()
    private val dialogManagerDisabled by inject<DialogManagerDisabled>()

    override val dialogHistory =
        MutableStateFlow<ImmutableList<Pair<DialogServiceMiddlewareAction, DialogManagerState>>>(
            persistentListOf()
        )

    override val logger = LogType.DialogManagerService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    override val serviceState = _serviceState.readOnly

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _currentDialogState = MutableStateFlow<DialogManagerState>(IdleState())
    override val currentDialogState = _currentDialogState.readOnly

    override fun start() {
        _serviceState.value = ServiceState.Success
        coroutineScope.launch {
            transitionTo(
                SessionEnded(Source.Local),
                get<IStateTransition>().transitionToIdleState(null)
            )
        }
    }

    override fun transitionTo(action: DialogServiceMiddlewareAction, state: DialogManagerState) {
        _currentDialogState.value = state
        dialogHistory.update {
            it.updateList { add(Pair(action, state)) }
        }
    }

    override fun onAction(action: DialogServiceMiddlewareAction) {
        coroutineScope.launch {
            when (ConfigurationSetting.dialogManagementOption.value) {
                Local      -> dialogManagerLocal.onAction(action)
                RemoteMQTT -> dialogManagerRemoteMqtt.onAction(action)
                Disabled   -> dialogManagerDisabled.onAction(action)
            }
        }
    }

    override suspend fun informMqtt(
        sessionData: SessionData?,
        action: DialogServiceMiddlewareAction
    ) {
        if (action.source !is Source.Mqtt) {
            if (sessionData != null) {
                when (action) {
                    is AsrError               -> mqttService.asrError(sessionData.sessionId)
                    is AsrTextCaptured        -> mqttService.asrTextCaptured(
                        sessionData.sessionId,
                        action.text
                    )

                    is WakeWordDetected       -> mqttService.hotWordDetected(action.wakeWord)
                    is IntentRecognitionError -> mqttService.intentNotRecognized(sessionData.sessionId)
                    is SessionEnded           -> mqttService.sessionEnded(sessionData.sessionId)
                    is SessionStarted         -> mqttService.sessionStarted(sessionData.sessionId)
                    is PlayFinished           -> mqttService.playFinished()
                    else                      -> Unit
                }
            } else {
                when (action) {
                    is WakeWordDetected -> mqttService.hotWordDetected(action.wakeWord)
                    is PlayFinished     -> mqttService.playFinished()
                    else                -> Unit
                }
            }
        }

    }

}