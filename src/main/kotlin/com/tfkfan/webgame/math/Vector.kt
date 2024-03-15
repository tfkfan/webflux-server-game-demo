package com.tfkfan.webgame.math

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

const val RAD_TO_DEG: Double = 180 / Math.PI
fun diff(v1: Vector, v2: Vector): Vector {
    return Vector(v2.x - v1.x, v2.y - v1.y)
}

fun getDistance(vector1: Vector, vector2: Vector): Double {
    return sqrt((vector2.x - vector1.x).pow(2.0) + (vector2.y - vector1.y).pow(2.0))
}

fun getLength(vector: Vector): Double {
    return sqrt(vector.x.pow(2.0) + vector.y.pow(2.0))
}

fun normalize(v1: Vector): Vector {
    val length = getLength(v1)
    if (length != 0.0) return Vector(v1.x / length, v1.y / length)

    return Vector(0.0, 0.0)
}

fun multiply(vector: Vector, scalar: Double): Vector {
    return Vector(vector.x * scalar, vector.y * scalar)
}

fun divide(vector: Vector, scalar: Double): Vector {
    return Vector(vector.x / scalar, vector.y / scalar)
}

fun scalar(v1: Vector, v2: Vector): Double {
    return v1.x * v2.x + v1.y * v2.y
}

fun angle(v1: Vector, v2: Vector): Double {
    return RAD_TO_DEG * acos(
        scalar(
            v1,
            v2
        ) / (getLength(v1) * getLength(
            v2
        ))
    )
}

class Vector : Serializable {
    var x: Double = 0.0
    var y: Double = 0.0

    constructor() {
        y = 0.0
        x = y
    }

    constructor(x: Double, y: Double) {
        set(x, y)
    }

    constructor(vector: Vector) {
        set(vector)
    }

    fun set(vector: Vector): Vector {
        this.x = vector.x
        this.y = vector.y
        return this
    }

    fun set(x: Double, y: Double): Vector {
        this.x = x
        this.y = y
        return this
    }

    fun sumX(x: Double): Vector {
        this.x += x
        return this
    }

    fun sumY(y: Double): Vector {
        this.y += y
        return this
    }

    fun sum(vector: Vector): Vector {
        sumX(vector.x)
        sumY(vector.y)
        return this
    }

    fun sumNew(vector: Vector): Vector {
        val v = Vector(this)
        v.sumX(vector.x)
        v.sumY(vector.y)
        return v
    }

    fun sum(x: Double, y: Double): Vector {
        sumX(x)
        sumY(y)
        return this
    }

    fun sumNew(x: Double, y: Double): Vector {
        val v = Vector(this)
        v.sumX(x)
        v.sumY(y)
        return v
    }

    fun multiplyX(x: Double): Vector {
        this.x *= x
        return this
    }

    fun multiplyY(y: Double): Vector {
        this.y *= y
        return this
    }

    fun multiply(vector: Vector): Vector {
        multiplyX(vector.x)
        multiplyY(vector.y)
        return this
    }

    fun multiply(x: Double, y: Double): Vector {
        multiplyX(x)
        multiplyY(y)
        return this
    }

    fun divideX(x: Double): Vector {
        this.x /= x
        return this
    }

    fun divideY(y: Double): Vector {
        this.y /= y
        return this
    }

    fun divide(vector: Vector): Vector {
        divideX(vector.x)
        divideY(vector.y)
        return this
    }

    fun divide(x: Double, y: Double): Vector {
        divideX(x)
        divideY(y)
        return this
    }

    fun diff(vector: Vector): Vector {
        diff(vector.x, vector.y)
        return this
    }

    fun diff(x: Double, y: Double): Vector {
        diffX(x)
        diffY(y)
        return this
    }

    fun diffX(x: Double): Vector {
        this.x -= x
        return this
    }

    fun diffY(y: Double): Vector {
        this.y -= y
        return this
    }

    fun reduce(value: Double): Vector {
        val valueNew = abs(value)
        if (x > 0) diffX(valueNew)
        else if (x < 0) sumX(valueNew)

        if (y > 0) diffY(valueNew)
        else if (y < 0) sumY(valueNew)
        return this
    }

    fun inverse(): Vector {
        this.x = -this.x
        this.y = -this.y
        return this
    }

    fun normalize(): Vector {
        set(normalize(this))
        return this
    }

    fun normalizeNew(): Vector {
        return normalize(this)
    }

    fun inverseNew(): Vector {
        return Vector(-x, -y)
    }

    fun inverse(multiplicator: Double) {
        x = (-multiplicator * x)
        y = (-multiplicator * y)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other == null || javaClass != other.javaClass) return false

        val vector = other as Vector

        return EqualsBuilder().append(x, vector.x).append(y, vector.y).isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37).append(x).append(y).toHashCode()
    }

}
