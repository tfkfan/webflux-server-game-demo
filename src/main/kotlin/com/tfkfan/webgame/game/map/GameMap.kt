package com.tfkfan.webgame.game.map

import com.tfkfan.webgame.game.model.DefaultPlayer
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Baltser Artem tfkfan
 */
class GameMap {

    private val players: MutableMap<Long, DefaultPlayer> = ConcurrentHashMap()

    fun getPlayerById(id: Long): DefaultPlayer? = players[id]
    fun getPlayers(): Collection<DefaultPlayer> = players.values
    fun addPlayer(player: DefaultPlayer) {
        players[player.id] = player
    }

    fun removePlayer(player: DefaultPlayer) = players.remove(player.id)

    fun nextPlayerId(): Long = (System.currentTimeMillis() shl 20) or (System.nanoTime() and 9223372036854251520L.inv())
    fun alivePlayers(): Long = players.values.stream().filter { it.isAlive }.count()
}
