package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService

class LearningPresenter(
    private val databaseService: DatabaseService
) {
    private var previousCategory : Category? = null
    suspend fun displayNextPicture(activity: Activity, view: ImageView) {
        val cats = databaseService.getCategories()
        var rndCat: Category?
        do {
            rndCat = cats.random()
        } while (previousCategory == rndCat)
        Log.d("displayNextPicture", "previous : $previousCategory, curr : $rndCat")
        previousCategory = rndCat

        databaseService.getPicture(rndCat!!)?.displayOn(activity, view)
        view.contentDescription = rndCat.name
        view.invalidate()
    }
}
