package com.github.HumanLearning2021.HumanLearningApp.model

import kotlinx.parcelize.Parcelize
import java.lang.IllegalArgumentException

@Parcelize
data class DummyDataset(override val id: Id, override val name: String, override val categories: Set<Category>) : Dataset