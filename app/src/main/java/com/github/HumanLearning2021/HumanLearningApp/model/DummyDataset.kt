package com.github.HumanLearning2021.HumanLearningApp.model

import kotlinx.parcelize.Parcelize
import java.lang.IllegalArgumentException

@Parcelize
data class DummyDataset(override val id: String, override val name: String, override val categories: Set<Category>
) : Dataset