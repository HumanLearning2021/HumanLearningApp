package com.github.HumanLearning2021.HumanLearningApp.room

import com.github.HumanLearning2021.HumanLearningApp.model.Category
import kotlinx.parcelize.Parcelize

@Parcelize
class OfflineCategory(override val id: String, override val name: String) : Category {
}