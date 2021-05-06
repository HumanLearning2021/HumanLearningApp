package com.github.HumanLearning2021.HumanLearningApp.offline

import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import kotlinx.parcelize.Parcelize

@Parcelize
class OfflineDataset(
    override val id: String,
    override val name: String,
    override val categories: Set<OfflineCategory>
) : Dataset