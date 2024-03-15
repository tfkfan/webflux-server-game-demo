package com.tfkfan.webgame.game

import com.tfkfan.webgame.network.pack.InitPack

interface Initializable<T : InitPack> {
    fun init(): T
}
