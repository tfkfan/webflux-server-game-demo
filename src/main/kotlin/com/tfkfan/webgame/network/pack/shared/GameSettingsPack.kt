package com.tfkfan.webgame.network.pack.shared

import com.tfkfan.webgame.network.pack.InitPack

data class GameSettingsPack(
    val loopRate: Long
) : InitPack
