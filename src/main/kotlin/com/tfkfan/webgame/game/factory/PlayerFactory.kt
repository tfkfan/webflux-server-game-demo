package com.tfkfan.webgame.game.factory

import com.tfkfan.webgame.game.model.Player
import com.tfkfan.webgame.game.room.GameRoom
import com.tfkfan.webgame.network.shared.UserSession

interface PlayerFactory<CM, P : Player, GR : GameRoom> {
    fun create(nextId: Long, initialData: CM, gameRoom: GR, playerSession: UserSession): P
}