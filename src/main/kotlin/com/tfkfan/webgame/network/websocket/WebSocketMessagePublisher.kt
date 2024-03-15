package com.tfkfan.webgame.network.websocket

import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.shared.MessageType
import java.util.function.Function

/**
 * @author Baltser Artem tfkfan
 */
interface WebSocketMessagePublisher {
    fun send(userSession: UserSession, message: Any)
    fun sendFailure(userSession: UserSession, message: Any)
    fun sendBroadcast(type: MessageType, message: String)
    fun sendBroadcast(message: Any)
    fun sendBroadcast(messageFunction: Function<UserSession, Any>)
    fun sendBroadcast(userSessions: Collection<UserSession>, message: Any)
    fun send(userSession: UserSession, function: Function<UserSession, Any>)
    fun sendBroadcast(userSessions: Collection<UserSession>, function: Function<UserSession, Any>)
}
