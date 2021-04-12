package com.github.HumanLearning2021.HumanLearningApp.room

import android.app.Activity
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import kotlinx.parcelize.Parcelize

@Parcelize
class OfflineCategorizedPicture(override val category: Category) : CategorizedPicture {
    override fun displayOn(activity: Activity, imageView: ImageView) {
        TODO("Not yet implemented")
    }
}