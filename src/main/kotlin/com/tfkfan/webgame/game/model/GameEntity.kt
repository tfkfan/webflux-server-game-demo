package com.tfkfan.webgame.game.model

import com.tfkfan.webgame.game.Initializable
import com.tfkfan.webgame.game.room.GameRoom
import com.tfkfan.webgame.network.pack.InitPack
import com.tfkfan.webgame.network.pack.UpdatePack
import com.tfkfan.webgame.network.pack.init.IInitPackProvider
import com.tfkfan.webgame.network.pack.update.IUpdatePackProvider
import com.tfkfan.webgame.math.Vector

abstract class GameEntity<ID, GR : GameRoom, IP : InitPack, UP : UpdatePack>(id: ID, protected var gameRoom: GR) :
    AbstractEntity<ID>(id), Initializable<IP>,
    IUpdatePackProvider<UP>, IInitPackProvider<IP> {
    var isMoving: Boolean = false
    var isAlive: Boolean = true
    open var position: Vector = Vector(0.0, 0.0)
    var velocity: Vector = Vector()
    var acceleration: Vector = Vector()
    var angularVelocity: Double = 0.0
}
