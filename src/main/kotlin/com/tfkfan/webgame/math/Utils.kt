package com.tfkfan.webgame.math

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

val mapType: Type get() = object : TypeToken<Map<String, Any>>() {}.type

fun getRandomIndex(max: Int): Int {
    return getRandomNumber(0, max)
}

fun getRandomNumber(min: Int, max: Int): Int {
    return ((Math.random() * (max - min)) + min).toInt()
}

fun getRandomNumber(min: Double, max: Double): Double {
    return ((Math.random() * (max - min)) + min)
}

