package com.tfkfan.webgame.network.pack.update

import com.tfkfan.webgame.network.pack.UpdatePack

interface IUpdatePackProvider<T : UpdatePack> {
    fun getUpdatePack(): T
}
