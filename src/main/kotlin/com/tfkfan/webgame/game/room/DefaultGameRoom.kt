package com.tfkfan.webgame.game.room

import com.tfkfan.webgame.config.*
import com.tfkfan.webgame.event.KeyDownPlayerEvent
import com.tfkfan.webgame.game.map.GameMap
import com.tfkfan.webgame.game.model.DefaultPlayer
import com.tfkfan.webgame.math.Vector
import com.tfkfan.webgame.network.pack.init.GameInitPack
import com.tfkfan.webgame.network.pack.shared.GameRoomPack
import com.tfkfan.webgame.network.pack.shared.GameSettingsPack
import com.tfkfan.webgame.network.pack.update.GameUpdatePack
import com.tfkfan.webgame.network.shared.Message
import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.service.RoomService
import com.tfkfan.webgame.service.WebSocketSessionService
import com.tfkfan.webgame.shared.Direction
import reactor.core.Disposable
import reactor.core.scheduler.Scheduler
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class DefaultGameRoom(
    val map: GameMap,
    gameRoomId: UUID,
    roomService: RoomService,
    webSocketSessionService: WebSocketSessionService,
    private val schedulerService: Scheduler,
    val gameProperties: GameProperties,
    val roomProperties: RoomProperties
) : AbstractGameRoom(gameRoomId, roomService, webSocketSessionService) {
    private val started = AtomicBoolean(false)
    private val roomFutureList: MutableList<Disposable> = ArrayList()

    override fun onRoomCreated(userSessions: List<UserSession>) {
        if (userSessions.isNotEmpty()) {
            userSessions.forEach {
                val defaultPlayer = it.player as DefaultPlayer
                defaultPlayer.position = Vector(100.0, 100.0)
                map.addPlayer(defaultPlayer)
            }
        }

        super.onRoomCreated(userSessions)

        sendBroadcast {
            Message(
                GAME_ROOM_JOIN_SUCCESS,
                GameSettingsPack(
                    roomProperties.loopRate
                )
            )
        }

        roomFutureList.add(
            schedulerService.schedulePeriodically(
                this,
                roomProperties.initDelay,
                roomProperties.loopRate,
                TimeUnit.MILLISECONDS
            )
        )
        roomFutureList.add(
            schedulerService.schedule(
                { roomService.onBattleEnd(this) },
                roomProperties.endDelay + roomProperties.startDelay,
                TimeUnit.MILLISECONDS
            )
        )
        log.trace("Room {} has been created", key())
    }

    private fun schedule(runnable: Runnable, delayMillis: Long) =
        roomFutureList.add(schedulerService.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS))

    override fun onRoomStarted() {
        started.set(false)
        sendBroadcast(
            Message(
                GAME_ROOM_START,
                GameRoomPack(
                    OffsetDateTime.now().plus(roomProperties.startDelay, ChronoUnit.MILLIS).toInstant().toEpochMilli()
                )
            )
        )
        schedule({ this.onBattleStarted() }, roomProperties.startDelay)
        log.trace("Room {} has been started", key())
    }

    override fun onBattleStarted() {
        log.trace("Room {}. Battle has been started", key())
        started.set(true)
        sendBroadcast(
            Message(
                GAME_ROOM_BATTLE_START, GameRoomPack(
                    OffsetDateTime.now().plus(roomProperties.endDelay, ChronoUnit.MILLIS).toInstant().toEpochMilli()
                )
            )
        )
    }

    //room's game loop
    override fun update() {
        if (!started.get()) return
        for (currentPlayer in map.getPlayers()) {
            if (currentPlayer.isAlive) currentPlayer.update()
            val updatePack = currentPlayer.getPrivateUpdatePack()
            val playerUpdatePackList = map.getPlayers()
                .map { it.getUpdatePack() }

            val session = currentPlayer.userSession
            send(
                session, Message(
                    UPDATE,
                    GameUpdatePack(
                        updatePack,
                        playerUpdatePackList
                    )
                )
            )
        }
    }

    fun onPlayerKeyDown(userSession: UserSession, event: KeyDownPlayerEvent) {
        if (!started.get()) return
        val player = userSession.player as DefaultPlayer
        if (!player.isAlive) return
        val direction = Direction.valueOf(event.inputId)
        player.updateState(direction, event.state)
    }

    fun onPlayerInitRequest(userSession: UserSession) {
        send(
            userSession, Message(
                INIT,
                GameInitPack(
                    (userSession.player as DefaultPlayer).getInitPack(),
                    roomProperties.loopRate,
                    map.alivePlayers()
                )
            )
        )
    }

    override fun onDestroy(userSessions: List<UserSession>) {
        userSessions.forEach { userSession: UserSession ->
            map.removePlayer(
                userSession.player as DefaultPlayer
            )
        }
        super.onDestroy(userSessions)
    }

    override fun close(): Collection<UserSession> {
        roomFutureList.forEach { it.dispose() }
        log.trace("Room {} has been closed", key())
        return super.close()
    }

    override fun onClose(userSession: UserSession) {
        send(userSession, Message(GAME_ROOM_CLOSE))
        super.onClose(userSession)
    }
}
