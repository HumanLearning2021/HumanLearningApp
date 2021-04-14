package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.LearningMode


class LearningPresenter(
    private val activity: Activity,
    private val learningMode: LearningMode,
    private val dataset: Dataset
) {
    private lateinit var previousCategory: Category

    // TODO Use injection !
    private val dbMgt = DummyDatabaseManagement.staticDummyDatabaseManagement

    /**
     * Picks a random picture from the dataset, and displays it on the given view
     * @param view The view on which to display the chosen picture. Normally has id R.id.learning_im_to_sort
     */
    suspend fun displayNextPicture(view: ImageView) {
        var rndCat: Category = getRandomCategory()

        val nextPicture = when (learningMode) {
            LearningMode.REPRESENTATION -> {
                val pics = dbMgt.getAllPictures(rndCat)
                // I don't really know how to smoothly treat the case when there are no pictures in
                // the category.
                if (pics.isEmpty()) throw IllegalStateException("There cannot be a category without pictures")
                pics.random()
            }
            LearningMode.PRESENTATION -> dbMgt.getRepresentativePicture(rndCat.id)
        }

        nextPicture!!.displayOn(activity, view)
        view.contentDescription = rndCat.name
        view.invalidate()
    }

    private fun getRandomCategory(): Category {
        var rndCat: Category?
        do {
            rndCat = dataset.categories.random()
        } while (::previousCategory.isInitialized && previousCategory == rndCat)
        previousCategory = rndCat!!
        return rndCat
    }

    /**
     * Displays the representative of the given category on the given ImageView
     * @param view The ImageView on which the representative is going to be displayed
     * @param category The category whose representative will be displayed
     */
    suspend fun displayTargetPicture(view: ImageView, category: Category) {
        dbMgt.getRepresentativePicture(category.id)?.displayOn(activity, view)
    }
}