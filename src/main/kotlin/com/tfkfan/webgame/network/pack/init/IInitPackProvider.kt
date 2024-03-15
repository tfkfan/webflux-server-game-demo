package com.tfkfan.webgame.network.pack.init

import com.tfkfan.webgame.network.pack.InitPack

interface IInitPackProvider<T : InitPack> {
    fun getInitPack(): T
}
