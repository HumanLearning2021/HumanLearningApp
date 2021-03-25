package com.github.HumanLearning2021.HumanLearningApp.model

import java.io.Serializable

interface Dataset: Serializable {
    val name: String
    val categories: Set<Category>
}