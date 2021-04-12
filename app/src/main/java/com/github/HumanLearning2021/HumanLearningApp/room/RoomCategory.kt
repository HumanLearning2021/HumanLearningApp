package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*

@Entity(tableName = "category")
data class RoomCategory(
    @PrimaryKey val id: String,

    val name: String
)

@Dao
interface CategoryDao {
    @Insert
    fun insertAll(vararg categories: RoomCategory)

    @Delete
    fun delete(category: RoomCategory)
}