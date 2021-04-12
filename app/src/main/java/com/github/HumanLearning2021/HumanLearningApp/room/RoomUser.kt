package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*
import com.github.HumanLearning2021.HumanLearningApp.model.User

@Entity(tableName = "user")
data class RoomUser(
    @PrimaryKey val uid: String,

    val type: User.Type,
    val displayName: String?,
    val email: String?
)

@Dao
interface UserDao {
    @Insert
    fun insertAll(vararg users: RoomUser)

    @Delete
    fun delete(user: RoomUser)
}