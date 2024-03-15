package com.tfkfan.webgame.network.pack.update

import com.tfkfan.webgame.network.pack.PrivateUpdatePack

interface IPrivateUpdatePackProvider<T : PrivateUpdatePack> {
    fun getPrivateUpdatePack(): T
}
