package com.tfkfan.webgame.game.room

/**
 * @author Baltser Artem tfkfan
 */

import com.tfkfan.webgame.game.Updatable
import com.tfkfan.webgame.network.shared.UserSession
import java.util.*
import java.util.function.Function


interface GameRoom : Runnable, Updatable {
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
    fun send(userSession: UserSession, message: Any)
    fun sendBroadcast(message: Any)
    fun sendBroadcastMapped(function: Function<UserSession, Any>)
    fun close(): Collection<UserSession>
    fun onClose(userSession: UserSession)
}

