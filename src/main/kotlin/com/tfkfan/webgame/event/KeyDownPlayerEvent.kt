package com.tfkfan.webgame.event

data class KeyDownPlayerEvent(val inputId: String, val state: Boolean) :
    AbstractEvent()