package com.tfkfan.webgame.service.impl

import com.tfkfan.webgame.config.FAILURE
import com.tfkfan.webgame.config.MESSAGE
import com.tfkfan.webgame.game.model.DefaultPlayer
import com.tfkfan.webgame.network.shared.Message
import com.tfkfan.webgame.network.pack.shared.GameMessagePack
import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.service.RoomService
import com.tfkfan.webgame.service.WebSocketSessionService
import com.tfkfan.webgame.shared.MessageType
import org.apache.logging.log4j.LogManager
import org.reactivestreams.Subscription
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.Many
import java.security.Principal
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

/**
 * @author Baltser Artem tfkfan
 */
@Service
open class WebSocketSessionServiceImpl : WebSocketSessionService {
    companion object {
        val log = LogManager.getLogger(this::class.java)
    }

    private var sessionPublishers: ConcurrentHashMap<String, Many<Any>> = ConcurrentHashMap()
    private var sessionSubscriptions: ConcurrentHashMap<String, Subscription> = ConcurrentHashMap()
    private var sessions: ConcurrentHashMap<String, UserSession> = ConcurrentHashMap()
    private lateinit var roomService: RoomService

    override fun sendBroadcast(message: Any) = sessionPublishers.values.forEach { it.tryEmitNext(message) }
    override fun close(userSession: UserSession): Mono<Void> {
        if (sessionSubscriptions.containsKey(userSession.id))
            sessionSubscriptions[userSession.id]!!.cancel()
        return Mono.empty()
    }

    override fun close(userSessionId: String): Mono<Void> {
        if (sessions.containsKey(userSessionId))
            return close(sessions[userSessionId]!!)
        return Mono.empty()
    }

    override fun closeAll(): Mono<Void> {
        sessions.values.forEach(this::close)
        return Mono.empty()
    }

    override fun roomIds(): Mono<Collection<String>> = Mono.fromCallable {
        roomService.getRoomIds()
    }

    override fun sessionIds(): Mono<Collection<String>> = Mono.fromCallable {
        sessions.keys.toList()
    }

    override fun roomSessionIds(roomId: UUID): Mono<Collection<String>> = Mono.fromCallable {
        roomService.getRoomSessionIds(roomId)
    }

    override fun sendBroadcast(messageFunction: Function<UserSession, Any>) =
        sessions.values.stream().forEach { this.send(it, messageFunction) }

    override fun sendBroadcast(userSessions: Collection<UserSession>, message: Any) =
        userSessions.forEach { send(it, message) }

    override fun send(
        userSession: UserSession,
        function: Function<UserSession, Any>
    ) =
        send(userSession, function.apply(userSession))

    override fun sendBroadcast(
        userSessions: Collection<UserSession>,
        function: Function<UserSession, Any>
    ) = userSessions.forEach { send(it, function.apply(it)) }

    override fun send(userSession: UserSession, message: Any) {
        val webSocketSessionId = userSession.id
        if (sessionPublishers.containsKey(webSocketSessionId)) sessionPublishers[webSocketSessionId]!!
            .tryEmitNext(message)
    }

    override fun sendFailure(userSession: UserSession, message: Any) =
        send(userSession, Message(FAILURE, message))

    override fun sendBroadcast(type: MessageType, message: String) =
        sendBroadcast(Message(MESSAGE, GameMessagePack(type.type, message)))

    override fun onActive(userSession: UserSession): Flux<Any> {
        log.debug("Client ${userSession.id} connected")
        /*        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(session.getHandshakeInfo().getUri());
Map<String,String> queryParams = builder.build().getQueryParams().toSingleValueMap();*/
        val sink = Sinks.many().unicast().onBackpressureBuffer<Any>()
        sessionPublishers[userSession.id] = sink
        sessions[userSession.id] = userSession
        return sink.asFlux()
    }

    override fun onSubscribe(userSession: UserSession, subscription: Subscription) {
        sessionSubscriptions[userSession.id] = subscription
    }

    override fun onPrincipalInit(
        userSession: UserSession,
        principal: Principal
    ) {
        userSession.principal = principal
    }

    override fun onInactive(userSession: UserSession) {
        log.debug("Client ${userSession.id} disconnected")
        if (!sessionPublishers.containsKey(userSession.id)) return
        sessionPublishers.remove(userSession.id)
        sessions.remove(userSession.id)

        if (userSession.roomKey != null)
            roomService.getRoomByKey(userSession.roomKey!!)
                .ifPresent { it.onDisconnect(userSession) }
    }

    @Autowired
    fun setGameRoomManagementService(@Lazy roomService: RoomService) {
        this.roomService = roomService
    }
}
