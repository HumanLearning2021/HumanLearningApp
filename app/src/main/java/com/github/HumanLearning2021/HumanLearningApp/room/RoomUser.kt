package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*
import com.github.HumanLearning2021.HumanLearningApp.model.User

@Entity(tableName = "user")
data class RoomUser(
    @PrimaryKey val userId: String,

    val type: User.Type,
    val displayName: String?,
    val email: String?
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun loadAll(): List<User>

    @Query("SELECT * FROM user WHERE userId = :id LIMIT 1")
    fun loadById(id: String): User

    @Query("SELECT * FROM user WHERE type == :type")
    fun loadByType(type: User.Type): List<User>

    @Transaction
    fun updateUser()

    @Insert
    fun insertAll(vararg users: RoomUser)

    @Delete
    fun delete(user: RoomUser)
}