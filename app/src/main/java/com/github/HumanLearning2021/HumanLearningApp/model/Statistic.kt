package com.github.HumanLearning2021.HumanLearningApp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class Event {
    /** the learner correctly performed an association */
    SUCCESS,

    /** the learner made an mistake */
    MISTAKE,
}

/**
 * An entity that stores a user's performance on a given dataset
 * @param user primary key designating the user
 * @param dataset primary key designating the dataset
 */
@Parcelize
data class Statistic(
    val id: Id,
    val occurrences: Map<Event, Int>,
) : Parcelable {
    @Parcelize
    data class Id(val userId: User.Id, val datasetId: DatasetId) : Parcelable {
        override fun toString() = "$userId+$datasetId"

        companion object {
            fun fromString(s: String): Id {
                val parts = s.split('+')
                return Id(User.Id.fromString(parts[0]), parts[1])
            }
        }
    }
}
