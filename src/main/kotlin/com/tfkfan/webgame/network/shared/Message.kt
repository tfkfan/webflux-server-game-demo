package com.tfkfan.webgame.network.shared

/**
 * @author Baltser Artem tfkfan
 */

open class Message {
    var type = 0
    var data: Any? = null

    constructor()
    constructor(type: Int) : this(type, null)
    constructor(type: Int, data: Any?) {
        this.type = type
        this.data = data
    }

    override fun toString(): String = "Message [type=" + type + ", source=" + data.toString() + "]"
}
