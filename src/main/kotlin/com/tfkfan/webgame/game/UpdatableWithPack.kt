package com.tfkfan.webgame.game

import com.tfkfan.webgame.network.pack.UpdatePack

interface UpdatableWithPack<T : UpdatePack> {
    fun update(): T
}
