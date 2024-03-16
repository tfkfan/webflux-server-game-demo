package com.tfkfan.webgame.service.impl

import com.tfkfan.webgame.config.ApplicationProperties
import com.tfkfan.webgame.config.GAME_ROOM_JOIN_WAIT
import com.tfkfan.webgame.event.GameRoomJoinEvent
import com.tfkfan.webgame.game.factory.PlayerFactory
import com.tfkfan.webgame.game.map.GameMap
import com.tfkfan.webgame.game.model.DefaultPlayer
import com.tfkfan.webgame.game.model.Player
import com.tfkfan.webgame.game.room.DefaultGameRoom
import com.tfkfan.webgame.network.shared.Message
import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.service.RoomService
import com.tfkfan.webgame.service.WebSocketSessionService
import com.tfkfan.webgame.shared.WaitingPlayerSession
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.util.*

@Service
open class RoomServiceImpl(
    private val playerFactory: PlayerFactory<GameRoomJoinEvent, DefaultPlayer, DefaultGameRoom>,
    private val applicationProperties: ApplicationProperties,
    private val schedulerService: Scheduler
) : RoomService {
    private lateinit var webSocketSessionService: WebSocketSessionService

    companion object {
        val log: Logger = LogManager.getLogger(this::class.java)
    }

    private val gameRoomMap: MutableMap<UUID, DefaultGameRoom> = mutableMapOf()
    private val sessionQueue: Queue<WaitingPlayerSession> = ArrayDeque()
    override fun getRoomSessionIds(key: UUID?): Collection<String> = getRoomByKey(key).map { room ->
        room.sessions().map { it.id }
    }.orElse(listOf())

    override fun getRoomIds(): Collection<String> = gameRoomMap.keys.map { it.toString() }
    override fun getRooms(): Collection<DefaultGameRoom> = gameRoomMap.values.toList()
    override fun getRoomByKey(key: UUID?): Optional<DefaultGameRoom> =
        if (key == null) Optional.empty() else Optional.ofNullable(gameRoomMap[key])

    override fun addPlayerToWait(userSession: UserSession, initialData: GameRoomJoinEvent) {
        sessionQueue.add(WaitingPlayerSession(userSession, initialData))
        webSocketSessionService.send(userSession, Message(GAME_ROOM_JOIN_WAIT))

        if (sessionQueue.size < applicationProperties.room.maxPlayers) return

        val gameMap = GameMap()
        val room = createRoom(gameMap)
        val userSessions: MutableList<UserSession> = ArrayList()
        while (userSessions.size.toLong() != applicationProperties.room.maxPlayers) {
            val waitingPlayerSession = sessionQueue.remove()
            val ps: UserSession = waitingPlayerSession.userSession
            val id: GameRoomJoinEvent = waitingPlayerSession.initialData
            val player: Player = playerFactory.create(gameMap.nextPlayerId(),  id, room, ps)
            ps.roomKey = room.key()
            ps.player = player
            userSessions.add(ps)
        }
        launchRoom(room, userSessions)
    }

    override fun removePlayerFromWaitQueue(session: UserSession) {
            sessionQueue.removeIf{waitingPlayerSession -> waitingPlayerSession.userSession == session }
    }

    private fun createRoom(gameMap: GameMap): DefaultGameRoom {
        val room = DefaultGameRoom(gameMap,
            UUID.randomUUID(), this, webSocketSessionService,
            schedulerService, applicationProperties.game,
            applicationProperties.room
        )
        gameRoomMap[room.key()] = room
        return room
    }

    private fun launchRoom(room: DefaultGameRoom, userSessions: List<UserSession>) {
        room.onRoomCreated(userSessions)
        room.onRoomStarted()
    }

    override fun onBattleEnd(room: DefaultGameRoom) {
        room.close()
        gameRoomMap.remove(room.key())
    }

    override fun close(key: UUID?): Mono<Void> {
        getRoomByKey(key).ifPresent {
            onBattleEnd(it)
        }
        return Mono.empty()
    }

    @Autowired
    fun setGameManager(@Lazy webSocketSessionService: WebSocketSessionService) {
        this.webSocketSessionService = webSocketSessionService
    }
}
