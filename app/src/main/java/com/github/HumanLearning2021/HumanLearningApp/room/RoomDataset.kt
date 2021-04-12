package com.github.HumanLearning2021.HumanLearningApp.room

import androidx.room.*
import com.github.HumanLearning2021.HumanLearningApp.model.Category

@Entity(tableName = "dataset")
data class RoomDataset(
    @PrimaryKey val id: String,

    val name: String,
    val categories: Set<Category>
)

@Dao
interface DatasetDao {
    @Insert
    fun insertAll(vararg datasets: RoomDataset)

    @Delete
    fun delete(dataset: RoomDataset)
}
