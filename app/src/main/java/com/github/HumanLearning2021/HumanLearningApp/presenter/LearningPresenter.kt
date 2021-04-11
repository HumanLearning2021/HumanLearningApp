package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.LearningMode


class LearningPresenter(
    private val activity: Activity,
    private val learningMode: LearningMode,
    private val dataset: Dataset
) {
    private lateinit var previousCategory : Category
    private val dbMgt = DummyDatabaseManagement.staticDummyDatabaseManagement

    /**
     * Picks a random picture from the dataset, and displays it on the given view
     * @param view The view on which to display the chosen picture. Normally has id R.id.learning_im_to_sort
     */
    suspend fun displayNextPicture(view: ImageView) {
        val cats = dataset.categories
        var rndCat: Category?
        do {
            rndCat = cats.random()
        } while (::previousCategory.isInitialized && previousCategory == rndCat)
        previousCategory = rndCat!!

        val nextPicture = when(learningMode){
            LearningMode.REPRESENTATION -> dbMgt.getAllPictures(rndCat).random()
            LearningMode.PRESENTATION -> dbMgt.getRepresentativePicture(rndCat.id)
        }

        nextPicture!!.displayOn(activity, view)
        view.contentDescription = rndCat.name
        view.invalidate()
    }

    /**
     * Displays the representative of the given category on the given ImageView
     * @param view The ImageView on which the representative is going to be displayed
     * @param category The category whose representative will be displayed
     */
    suspend fun displayTargetPicture(view: ImageView, category: Category){
        dbMgt.getRepresentativePicture(category.id)?.displayOn(activity, view)
    }
}