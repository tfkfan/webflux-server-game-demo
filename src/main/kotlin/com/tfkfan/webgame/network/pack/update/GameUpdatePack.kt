package com.tfkfan.webgame.network.pack.update

import com.tfkfan.webgame.network.pack.UpdatePack

data class GameUpdatePack(
    val player: PrivatePlayerUpdatePack,
    val players: Collection<PlayerUpdatePack>
) : UpdatePack
