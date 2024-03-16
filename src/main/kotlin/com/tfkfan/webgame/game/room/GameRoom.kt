package com.tfkfan.webgame.game.room

/**
 * @author Baltser Artem tfkfan
 */

import com.tfkfan.webgame.game.Updatable
import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.network.websocket.WebSocketMessagePublisher
import java.util.*
import java.util.function.Function


interface GameRoom : Runnable, Updatable, WebSocketMessagePublisher {
    fun onRoomCreated(userSessions: List<UserSession>)
    fun onRoomStarted()
    fun onBattleStarted()
    fun onDestroy(userSessions: List<UserSession>)
    fun onDisconnect(userSession: UserSession): UserSession
    fun sessions(): Collection<UserSession>
    fun currentPlayersCount(): Int
    fun getPlayerSessionBySessionId(userSession: UserSession): Optional<UserSession>
    fun key(): UUID
    fun key(key: UUID)
    fun close(): Collection<UserSession>
    fun onClose(userSession: UserSession)
}

