package com.tfkfan.webgame.shared

import com.tfkfan.webgame.event.GameRoomJoinEvent
import com.tfkfan.webgame.network.shared.UserSession

/**
 * @author Baltser Artem tfkfan
 */
data class WaitingPlayerSession(
    val userSession: UserSession,
    val initialData: GameRoomJoinEvent
)
