package com.tfkfan.webgame.network.shared

import com.tfkfan.webgame.game.model.AbstractEntity
import com.tfkfan.webgame.game.model.Player
import org.springframework.web.reactive.socket.HandshakeInfo
import java.security.Principal
import java.util.*

class UserSession(
    override val id: String,
    val handshakeInfo: HandshakeInfo
) : AbstractEntity<String>(id) {
    var locked: Boolean = false
    var player: Player? = null
    var roomKey: UUID? = null
    var principal: Principal? = null
    override fun toString(): String = "UserSession [id=" + id + "player=" + player + ", parentGameRoom=" + roomKey + "]"
}
