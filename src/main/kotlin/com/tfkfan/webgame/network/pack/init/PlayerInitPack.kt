package com.tfkfan.webgame.network.pack.init

import com.tfkfan.webgame.network.pack.InitPack
import com.tfkfan.webgame.math.Vector

data class PlayerInitPack(
    val id: Long,
    val position: Vector
) : InitPack
