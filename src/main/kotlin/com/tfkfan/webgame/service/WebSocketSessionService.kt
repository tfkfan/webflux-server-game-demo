package com.tfkfan.webgame.service

import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.network.websocket.WebSocketMessagePublisher
import org.reactivestreams.Subscription
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*

/**
 * @author Baltser Artem tfkfan
 */
interface WebSocketSessionService : WebSocketMessagePublisher {
    fun onActive(userSession: UserSession): Flux<Any>
    fun onSubscribe(userSession: UserSession, subscription: Subscription)
    fun onPrincipalInit(userSession: UserSession, principal: Principal)
    fun onInactive(userSession: UserSession)
    fun close(userSession: UserSession): Mono<Void>
    fun close(userSessionId: String): Mono<Void>
    fun closeAll():Mono<Void>
    fun roomIds():Mono<Collection<String>>
    fun sessionIds():Mono<Collection<String>>
    fun roomSessionIds(roomId:UUID):Mono<Collection<String>>
    fun ban(userSession: UserSession, seconds:Long):Mono<Void>
    fun ban(userSessionId: String, seconds:Long):Mono<Void>
}
