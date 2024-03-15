package com.tfkfan.webgame.game.model

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

abstract class AbstractEntity<I>(override val id: I) : Entity<I> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other == null || javaClass != other.javaClass) return false

        val that = other as AbstractEntity<*>

        return EqualsBuilder().append(id, that.id).isEquals
    }
    override fun hashCode(): Int =HashCodeBuilder(17, 37).append(id).toHashCode()
}
