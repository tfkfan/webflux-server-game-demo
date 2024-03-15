package com.tfkfan.webgame.network.pack.update

import com.tfkfan.webgame.network.pack.UpdatePack
import com.tfkfan.webgame.math.Vector

data class PlayerUpdatePack(
    val id: Long,
    val position: Vector
) : UpdatePack
