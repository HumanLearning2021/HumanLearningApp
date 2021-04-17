package com.github.HumanLearning2021.HumanLearningApp.offline

import com.github.HumanLearning2021.HumanLearningApp.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
class OfflineUser(
    override val type: User.Type,
    override val uid: String,
    override val displayName: String?,
    override val email: String?
) : User