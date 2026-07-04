package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryCapsuleDao {
    @Query("SELECT * FROM memory_capsules ORDER BY timestamp DESC")
    fun getAllCapsules(): Flow<List<MemoryCapsule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsule(capsule: MemoryCapsule)

    @Update
    suspend fun updateCapsule(capsule: MemoryCapsule)

    @Delete
    suspend fun deleteCapsule(capsule: MemoryCapsule)

    @Query("SELECT * FROM memory_capsules WHERE category = :category ORDER BY timestamp DESC")
    fun getCapsulesByCategory(category: String): Flow<List<MemoryCapsule>>
}

@Dao
interface FriendBubbleDao {
    @Query("SELECT * FROM friend_bubbles ORDER BY isOnline DESC, name ASC")
    fun getAllFriends(): Flow<List<FriendBubble>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendBubble)

    @Update
    suspend fun updateFriend(friend: FriendBubble)

    @Query("UPDATE friend_bubbles SET colorHex = :color, shapeType = :shape WHERE id = :id")
    suspend fun customizeFriend(id: Int, color: String, shape: String)

    @Query("UPDATE friend_bubbles SET latestMessage = :msg WHERE id = :id")
    suspend fun updateLatestMessage(id: Int, msg: String)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE friendId = :friendId ORDER BY timestamp ASC")
    fun getMessagesForFriend(friendId: Int): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE friendId = :friendId")
    suspend fun clearMessagesForFriend(friendId: Int)
}
