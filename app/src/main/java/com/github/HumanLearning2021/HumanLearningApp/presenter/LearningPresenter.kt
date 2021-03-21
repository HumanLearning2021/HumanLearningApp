package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatasetInterface
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatasetInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LearningPresenter {
    private lateinit var categories : Set<Category>
    private val datasetInterface: DatasetInterface

    constructor(datasetInterface: DatasetInterface, coroutineScope: CoroutineScope) {
        // load the categories when the presenter is constructed. My idea is that the LearningActivity
        // will be started with an intent referencing the dataset the person clicked on in a
        // different screen. So it's ok to load the categories on construction. And to change the
        // current dataset, the activity will need to be relaunched

        // TODO uncomment following line once PR#31 is merged
//        coroutineScope.launch { categories = datasetInterface.getCategories() }
        // TODO remove following once PR#31 merged
        val fork = DummyCategory("fork")
        val knife = DummyCategory("knife")
        val spoon = DummyCategory("spoon")
        categories = setOf(fork, knife, spoon)

        this.datasetInterface = datasetInterface
    }

    suspend fun displayNextPicture(activity: Activity, view: ImageView) {
        // safety check to be sure there is no race condition
        if(!this::categories.isInitialized) return
        val rndCat = categories.random()
        datasetInterface.getPicture(rndCat)?.displayOn(activity, view)
        view.contentDescription = rndCat.name
        view.invalidate()
    }
}
