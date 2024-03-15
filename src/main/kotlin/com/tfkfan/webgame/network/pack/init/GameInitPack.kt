package com.tfkfan.webgame.network.pack.init

import com.tfkfan.webgame.network.pack.InitPack

data class GameInitPack(
    val player: PlayerInitPack,
    val loopRate: Long,
    val playersCount: Long
) : InitPack
