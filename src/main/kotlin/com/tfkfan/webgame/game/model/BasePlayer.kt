package com.tfkfan.webgame.game.model

import com.tfkfan.webgame.game.Updatable
import com.tfkfan.webgame.game.room.GameRoom
import com.tfkfan.webgame.math.Vector
import com.tfkfan.webgame.network.pack.InitPack
import com.tfkfan.webgame.network.pack.PrivateUpdatePack
import com.tfkfan.webgame.network.pack.UpdatePack
import com.tfkfan.webgame.network.pack.update.IPrivateUpdatePackProvider
import com.tfkfan.webgame.network.shared.UserSession
import com.tfkfan.webgame.shared.Direction
import java.util.*
import java.util.stream.Collectors

abstract class BasePlayer<GR : GameRoom, IP : InitPack, UP : UpdatePack, PUP : PrivateUpdatePack>(
    id: Long,  gameRoom: GR,
    var userSession: UserSession
) : GameEntity<Long, GR, IP, UP>(id, gameRoom), Player, Updatable, IPrivateUpdatePackProvider<PUP> {
    lateinit var target: Vector
    lateinit var direction: Direction
    var movingState: MutableMap<Direction, Boolean> = Arrays.stream(Direction.values()).collect(
        Collectors.toMap(
            { direction: Direction -> direction },
            { false })
    )
}
