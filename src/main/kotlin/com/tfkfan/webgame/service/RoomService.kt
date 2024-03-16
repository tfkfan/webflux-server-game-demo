package com.tfkfan.webgame.service

import com.tfkfan.webgame.event.GameRoomJoinEvent
import com.tfkfan.webgame.game.room.DefaultGameRoom
import com.tfkfan.webgame.network.shared.UserSession
import reactor.core.publisher.Mono
import java.util.*

interface RoomService{
    fun getRoomSessionIds(key: UUID?):Collection<String>
    fun getRoomIds():Collection<String>
    fun getRooms():Collection<DefaultGameRoom>
    fun getRoomByKey(key: UUID?): Optional<DefaultGameRoom>
    fun addPlayerToWait(userSession: UserSession, initialData: GameRoomJoinEvent)
    fun removePlayerFromWaitQueue(session: UserSession)
    fun onBattleEnd(room: DefaultGameRoom)
    fun close(key: UUID?):Mono<Void>
}
