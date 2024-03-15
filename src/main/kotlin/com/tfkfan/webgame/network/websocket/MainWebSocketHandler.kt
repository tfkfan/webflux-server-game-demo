package com.tfkfan.webgame.network.websocket

import com.google.gson.Gson
import com.tfkfan.webgame.network.shared.Message
import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.service.RoomService
import com.tfkfan.webgame.service.WebSocketSessionService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.InputStreamReader

/**
 * @author Baltser Artem tfkfan
 */
@Service
class MainWebSocketHandler(
    private val webSocketSessionService: WebSocketSessionService
) : WebSocketHandler {
    private val objectMapper = Gson()
    private lateinit var roomService: RoomService

    companion object {
        val log: Logger = LogManager.getLogger(this::class.java)
    }

    override fun handle(webSocketSession: WebSocketSession): Mono<Void> {
        val input = webSocketSession.receive().share()
        val userSession = UserSession(webSocketSession.id, webSocketSession.handshakeInfo)
        val sessionHandler = UserSessionWebSocketHandler(
            userSession,
            webSocketSessionService, roomService
        )

        val receive = input
            .filter { it.type == WebSocketMessage.Type.TEXT }
            .map(this::toMessage)
            .doOnNext(sessionHandler::onNext)

        val send = webSocketSession.send(webSocketSessionService.onActive(userSession)
            .map {
                webSocketSession.textMessage(objectMapper.toJson(it))
            }
            .doOnError { handleError(webSocketSession, it) })

        val security = webSocketSession.handshakeInfo.principal.doOnNext {
            webSocketSessionService.onPrincipalInit(userSession, it)
        }
        return Flux.merge(receive, send, security)
            .takeUntil { userSession.locked }
            .doOnTerminate { webSocketSessionService.onInactive(userSession) }
            .doOnError { handleError(webSocketSession, it) }
            .then()
    }

    private fun toMessage(webSocketMessage: WebSocketMessage): Message =
        objectMapper.fromJson(InputStreamReader(webSocketMessage.payload.asInputStream()), Message::class.java)

    private fun handleError(webSocketSession: WebSocketSession, exception: Throwable) {
        log.error("Error in ${webSocketSession.id} session", exception)
    }

    @Autowired
    fun setGameRoomManagementService(@Lazy roomService: RoomService) {
        this.roomService = roomService
    }
}
