package com.tfkfan.webgame.game.room

import com.tfkfan.webgame.config.MESSAGE
import com.tfkfan.webgame.network.shared.Message
import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.service.RoomService
import com.tfkfan.webgame.service.WebSocketSessionService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

abstract class AbstractGameRoom protected constructor(
    private var gameRoomId: UUID,
    protected val roomService: RoomService,
    protected val webSocketSessionService: WebSocketSessionService
) : GameRoom {
    companion object {
        val log: Logger = LogManager.getLogger(this::class.java)
    }

    private var sessions: MutableMap<String, UserSession> = ConcurrentHashMap()

    override fun onRoomCreated(userSessions: List<UserSession>) {
        for (playerSession in userSessions) {
            this.sessions[playerSession.id] = playerSession
            sendBroadcast(Message(MESSAGE, playerSession.player!!.id.toString() + " successfully joined"))
        }
    }
    override fun onDestroy(userSessions: List<UserSession>) {
        for (playerSession in userSessions) {
            this.sessions.remove(playerSession.id)
            sendBroadcast(Message(MESSAGE, playerSession.player!!.id.toString() + " left"))
        }
    }
    override fun onDisconnect(userSession: UserSession): UserSession = sessions.remove(userSession.id)!!
    override fun send(userSession: UserSession, message: Any) =
        webSocketSessionService.send(userSession, message)
    override fun sendBroadcast(message: Any) =
        webSocketSessionService.sendBroadcast(sessions.values, message)
    override fun sendBroadcastMapped(function: Function<UserSession, Any>) =
        webSocketSessionService.sendBroadcast(sessions.values, function)
    override fun run() {
        try {
            update()
        } catch (e: Exception) {
            log.error("room update exception", e)
        }
    }
    override fun close(): Collection<UserSession> {
        val result: Collection<UserSession> = sessions.values
        sessions.values.forEach { this.onClose(it) }
        return result
    }
    override fun onClose(userSession: UserSession) {}
    override fun getPlayerSessionBySessionId(userSession: UserSession): Optional<UserSession> =
        if (sessions.containsKey(userSession.id)) Optional.of(sessions[userSession.id]!!)
        else Optional.empty()
    override fun sessions(): Collection<UserSession> =sessions.values
    override fun currentPlayersCount(): Int = sessions.size
    override fun key(): UUID = gameRoomId
    override fun key(key: UUID) {
        this.gameRoomId = key
    }
}
