package com.tfkfan.webgame.game.model

import com.tfkfan.webgame.config.ABS_PLAYER_SPEED
import com.tfkfan.webgame.game.map.GameMap
import com.tfkfan.webgame.game.room.DefaultGameRoom
import com.tfkfan.webgame.math.Vector
import com.tfkfan.webgame.network.pack.init.PlayerInitPack
import com.tfkfan.webgame.network.pack.update.PlayerUpdatePack
import com.tfkfan.webgame.network.pack.update.PrivatePlayerUpdatePack
import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.shared.Direction

class DefaultPlayer(
    id: Long, gameRoom: DefaultGameRoom, userSession: UserSession
) : BasePlayer<DefaultGameRoom, PlayerInitPack, PlayerUpdatePack, PrivatePlayerUpdatePack>(
    id, gameRoom, userSession
) {
    private val gameMap: GameMap

    init {
        position = Vector(0.0, 0.0)
        velocity = Vector()
        target = Vector()
        acceleration = Vector()
        direction = Direction.UP
        this.gameMap = gameRoom.map
    }

    fun handleTarget(target: Vector) {
        this.target = target
    }

    fun updateState(direction: Direction, state: Boolean) {
        movingState[direction] = state
        isMoving = movingState.containsValue(true)
    }

    override fun update() {
        velocity.x =
            if (isMoving && movingState[Direction.RIGHT] == true) ABS_PLAYER_SPEED else (if (isMoving && movingState[Direction.LEFT] == true) -ABS_PLAYER_SPEED else 0.0)
        velocity.y =
            if (isMoving && movingState[Direction.UP] == true) -ABS_PLAYER_SPEED else (if (isMoving && movingState[Direction.DOWN] == true) ABS_PLAYER_SPEED else 0.0)

        if (isMoving) position.sum(velocity)
    }

    override fun getUpdatePack(): PlayerUpdatePack = PlayerUpdatePack(id, position)

    override fun getInitPack(): PlayerInitPack = PlayerInitPack(
        id, position
    )

    override fun init(): PlayerInitPack = getInitPack()
    override fun getPrivateUpdatePack(): PrivatePlayerUpdatePack = PrivatePlayerUpdatePack(id)
}
