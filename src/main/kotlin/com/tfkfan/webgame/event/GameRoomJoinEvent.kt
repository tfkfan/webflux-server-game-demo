package com.tfkfan.webgame.event

import com.tfkfan.webgame.event.AbstractEvent

data class GameRoomJoinEvent(
    val reconnectKey: String?,
    val playerName: String
) : AbstractEvent()