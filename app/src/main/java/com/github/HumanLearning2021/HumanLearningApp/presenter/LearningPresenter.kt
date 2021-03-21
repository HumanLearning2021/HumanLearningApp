package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatasetInterface
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatasetInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LearningPresenter(
    private val datasetInterface: DatasetInterface
) {
    private var previousCategory : Category? = null
    suspend fun displayNextPicture(activity: Activity, view: ImageView) {
        val cats = datasetInterface.getCategories()
        var rndCat: Category?
        do {
            rndCat = cats.random()
        } while (previousCategory == rndCat)
        Log.d("displayNextPicture", "previous : $previousCategory, curr : $rndCat")
        previousCategory = rndCat

        datasetInterface.getPicture(rndCat!!)?.displayOn(activity, view)
        view.contentDescription = rndCat.name
        view.invalidate()
    }
}
