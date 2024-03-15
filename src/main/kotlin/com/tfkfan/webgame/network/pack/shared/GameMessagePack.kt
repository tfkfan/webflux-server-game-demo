package com.tfkfan.webgame.network.pack.shared

import com.tfkfan.webgame.network.pack.InitPack

data class GameMessagePack(
    val messageType: Int,
    val message: String
) : InitPack
