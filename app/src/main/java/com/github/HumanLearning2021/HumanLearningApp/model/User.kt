package com.github.HumanLearning2021.HumanLearningApp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Representation of a user.
 * Primary key: (type, uid)
 * @property displayName of the user
 * @property email of the user
 * @property uid unique identifier of the user
 * @property type of the user
 * @property isAdmin defines whether the user has administrator privileges
 */
@Parcelize
data class User(
    val displayName: String?,
    val email: String?,
    val uid: String,
    val type: Type,
    var isAdmin: Boolean

) : Parcelable {

    enum class Type {
        FIREBASE,
        TEST,
    }

    /**
     * Representation of a user Id
     * @property uid unique identifier of the user
     * @property type of the user
     */
    @Parcelize
    data class Id(val uid: String, val type: Type) : Parcelable {
        override fun toString() = "$uid@$type"

        companion object {
            fun fromString(s: String): Id {
                val parts = s.split('@')
                return Id(parts[0], Type.valueOf(parts[1]))
            }
        }
    }
}

val User.id: User.Id get() = User.Id(uid, type)