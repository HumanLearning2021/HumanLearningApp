package com.github.HumanLearning2021.HumanLearningApp.model

import android.os.Parcelable

/**
 * primary key: (type, uid)
 */
interface User: Parcelable {
  enum class Type {
    FIREBASE,
  }
  val type: Type
  val uid: String
  val displayName: String?
  val email: String?
}