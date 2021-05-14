package com.github.HumanLearning2021.HumanLearningApp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * primary key: (type, uid)
 */
@Parcelize
data class User(
    val displayName: String?,
    val email: String?,
    val uid: String,
    val type: Type,
) : Parcelable {
    
    enum class Type {
        FIREBASE,
        TEST,
    }

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