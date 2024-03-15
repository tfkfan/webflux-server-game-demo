package com.tfkfan.webgame.game.factory

import com.tfkfan.webgame.game.model.DefaultPlayer
import com.tfkfan.webgame.game.room.DefaultGameRoom
import com.tfkfan.webgame.event.GameRoomJoinEvent
import com.tfkfan.webgame.network.shared.UserSession
import org.springframework.stereotype.Component

@Component
class DefaultPlayerFactory : PlayerFactory<GameRoomJoinEvent, DefaultPlayer, DefaultGameRoom> {
    override fun create(
        nextId: Long,
        initialData: GameRoomJoinEvent,
        gameRoom: DefaultGameRoom,
        playerSession: UserSession
    ): DefaultPlayer = DefaultPlayer(nextId, gameRoom, playerSession)
}