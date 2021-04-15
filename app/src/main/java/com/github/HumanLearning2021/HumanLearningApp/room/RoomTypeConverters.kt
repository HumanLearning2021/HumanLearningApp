package com.github.HumanLearning2021.HumanLearningApp.room

import android.net.Uri
import androidx.room.TypeConverter
import com.github.HumanLearning2021.HumanLearningApp.model.User

class RoomTypeConverters {
    @TypeConverter
    fun fromUserType(type: User.Type): Int {
        return type.ordinal
    }

    @TypeConverter
    fun toUserType(ordinal: Int): User.Type {
        return User.Type.values()[ordinal]
    }

    @TypeConverter
    fun fromUri(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun toUri(path: String): Uri {
        return Uri.parse(path)
    }
}