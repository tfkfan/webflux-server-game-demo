package com.tfkfan.webgame.game.room

import com.tfkfan.webgame.config.MESSAGE
import com.tfkfan.webgame.network.shared.Message
import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.service.RoomService
import com.tfkfan.webgame.service.WebSocketSessionService
import com.tfkfan.webgame.shared.MessageType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import reactor.core.Disposable
import reactor.core.scheduler.Scheduler
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Function

abstract class AbstractGameRoom protected constructor(
    private var gameRoomId: UUID,
    private val schedulerService: Scheduler,
    protected val roomService: RoomService,
    protected val webSocketSessionService: WebSocketSessionService
) : GameRoom {
    private val roomFutureList: MutableList<Disposable> = ArrayList()
    companion object {
        val log: Logger = LogManager.getLogger(this::class.java)
    }

    private var sessions: MutableMap<String, UserSession> = HashMap()

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

    override fun schedule(runnable: Runnable, delayMillis: Long)=
        roomFutureList.add(schedulerService.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS))

    override fun schedulePeriodically(runnable: Runnable, initDelay: Long, loopRate: Long)=
        roomFutureList.add(schedulerService.schedulePeriodically(runnable,initDelay, loopRate,TimeUnit.MILLISECONDS))

    override fun onDisconnect(userSession: UserSession): UserSession = sessions.remove(userSession.id)!!
    override fun send(userSession: UserSession, message: Any) =
        webSocketSessionService.send(userSession, message)

    override fun sendBroadcast(message: Any) =
        webSocketSessionService.sendBroadcast(sessions.values, message)

    override fun sendBroadcast(messageFunction: Function<UserSession, Any>) =
        webSocketSessionService.sendBroadcast(sessions.values, messageFunction)

    override fun sendFailure(userSession: UserSession, message: Any) {
        webSocketSessionService.sendFailure(userSession, message)
    }

    override fun sendBroadcast(type: MessageType, message: String) {
       webSocketSessionService.sendBroadcast(type, message)
    }

    override fun sendBroadcast(userSessions: Collection<UserSession>, message: Any) {
        webSocketSessionService.sendBroadcast(userSessions, message)
    }

    override fun send(userSession: UserSession, function: Function<UserSession, Any>) {
        webSocketSessionService.send(userSession, function)
    }

    override fun sendBroadcast(userSessions: Collection<UserSession>, function: Function<UserSession, Any>) {
        webSocketSessionService.sendBroadcast(userSessions, function)
    }

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
        roomFutureList.forEach { it.dispose() }
        log.trace("Room {} has been closed", key())
        return result
    }

    override fun onClose(userSession: UserSession) {}
    override fun getPlayerSessionBySessionId(userSession: UserSession): Optional<UserSession> =
        if (sessions.containsKey(userSession.id)) Optional.of(sessions[userSession.id]!!)
        else Optional.empty()

    override fun sessions(): Collection<UserSession> = sessions.values
    override fun currentPlayersCount(): Int = sessions.size
    override fun key(): UUID = gameRoomId
    override fun key(key: UUID) {
        this.gameRoomId = key
    }
}
