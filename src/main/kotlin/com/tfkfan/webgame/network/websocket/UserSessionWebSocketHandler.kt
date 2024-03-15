package com.tfkfan.webgame.network.websocket

import com.google.gson.Gson
import com.tfkfan.webgame.event.GameRoomJoinEvent
import com.tfkfan.webgame.event.KeyDownPlayerEvent
import com.tfkfan.webgame.network.shared.Message
import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.service.RoomService
import com.tfkfan.webgame.service.WebSocketSessionService
import com.tfkfan.webgame.config.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class UserSessionWebSocketHandler(
    private val userSession: UserSession,
    private val webSocketSessionService: WebSocketSessionService,
    private val roomService: RoomService
) {
    private val objectMapper = Gson()

    companion object {
        val log: Logger = LogManager.getLogger(this::class.java)
    }

    fun onNext(message: Message) {
        val messageData = if (message.data != null) message.data as Map<*, *> else null
        val reconnectKey = messageData?.get("reconnectKey")?.toString()

        when (message.type) {
            GAME_ROOM_JOIN -> {
                    log.debug("Join attempt from {}", userSession.handshakeInfo.remoteAddress)
                    roomService.addPlayerToWait(
                        userSession,
                        objectMapper.fromJson(messageData.toString(), GameRoomJoinEvent::class.java)
                    )
            }

            INIT -> {
                roomService.getRoomByKey(userSession.roomKey)
                    .ifPresent { it.onPlayerInitRequest(userSession) }
            }

            PLAYER_KEY_DOWN -> {
                roomService.getRoomByKey(userSession.roomKey)
                    .ifPresent {
                        it.onPlayerKeyDown(
                            userSession,
                            objectMapper.fromJson(messageData.toString(), KeyDownPlayerEvent::class.java)
                        )
                    }
            }

        }
    }
}