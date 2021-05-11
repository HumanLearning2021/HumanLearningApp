package com.github.HumanLearning2021.HumanLearningApp.model

import kotlinx.parcelize.Parcelize

@Parcelize
data class DummyUser(
    override val displayName: String?,
    override val email: String?,
    override val uid: String,
    override val type: User.Type,
    override val isAdmin: Boolean,
) : User