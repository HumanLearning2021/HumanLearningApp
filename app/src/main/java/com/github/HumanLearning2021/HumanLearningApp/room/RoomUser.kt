package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*
import com.github.HumanLearning2021.HumanLearningApp.model.User

@Entity(tableName = "user", primaryKeys = ["userId", "type"])
data class RoomUser(
    val userId: String,
    val type: User.Type,
    val displayName: String?,
    val email: String?
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun loadAll(): List<RoomUser>

    @Query("SELECT * FROM user WHERE userId = :id AND type = :type LIMIT 1")
    fun load(id: String, type: User.Type): RoomUser

    @Update
    fun update(user: RoomUser)

    @Insert
    fun insertAll(vararg users: RoomUser)

    @Delete
    fun delete(user: RoomUser)
}