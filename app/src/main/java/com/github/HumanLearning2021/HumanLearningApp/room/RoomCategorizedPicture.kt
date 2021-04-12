package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import com.github.HumanLearning2021.HumanLearningApp.model.Category

@Entity(tableName = "categorizedPicture")
data class RoomCategorizedPicture(
    val category: Category
)

@Dao
interface CategorizedPictureDao {
    @Insert
    fun insertAll(vararg categorizedPictures: RoomCategorizedPicture)

    @Delete
    fun delete(categorizedPicture: RoomCategorizedPicture)
}