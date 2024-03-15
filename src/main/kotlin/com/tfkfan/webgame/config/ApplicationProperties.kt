package com.tfkfan.webgame.config

import org.springframework.boot.context.properties.ConfigurationProperties

data class GameProperties(
    val absPlayerSpeed: Double = ABS_PLAYER_SPEED,
    val absSkillSpeed: Double = ABS_SKILL_SPEED,
    val gameThreads: Int = 4
)

data class RoomProperties(
    val loopRate: Long = DEFAULT_LOOPRATE,
    val initDelay: Long = ROOM_INIT_DELAY,
    val startDelay: Long = ROOM_START_DELAY,
    val endDelay: Long = ROOM_END_DELAY,
    val maxPlayers: Long = MAX_PLAYERS
)

@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(val room: RoomProperties = RoomProperties(), val game: GameProperties = GameProperties())
