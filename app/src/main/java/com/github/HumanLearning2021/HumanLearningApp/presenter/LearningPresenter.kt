package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningMode
import javax.inject.Inject


class LearningPresenter @Inject constructor(
    @ScratchDatabase
    private val dbMgt: DatabaseManagement
) {
    // may be set by the view
    var learningMode = LearningMode.PRESENTATION

    // must be set by the view
    lateinit var dataset: Dataset

    private var previousCategory: Category? = null

    /**
     * Picks a random picture from the dataset, and displays it on the given view
     * @param view The view on which to display the chosen picture. Normally has id R.id.learning_im_to_sort
     */
    suspend fun displayNextPicture(activity: Activity, view: ImageView) {
        val (rndCat, catPicsIds) = getRndCategoryWithPictureIds()

        val nextPicture = when (learningMode) {
            LearningMode.REPRESENTATION -> dbMgt.getPicture(catPicsIds.random())
            LearningMode.PRESENTATION -> dbMgt.getRepresentativePicture(rndCat.id)
        }

        nextPicture!!.displayOn(activity, view)
        view.contentDescription = rndCat.name
        view.invalidate()
    }

    /**
     * Returns a random category of the dataset, with the corresponding list of picture *ids*.
     * The returned category is guaranteed to be different from the previousCategory
     * The list of picture ids is guaranteed to ben non-empty
     */
    private suspend fun getRndCategoryWithPictureIds(): Pair<Category, List<Any>> {
        var rndCat: Category?
        var catPicsIds: List<Any>
        do {
            rndCat = dataset.categories.random()
            catPicsIds = dbMgt.getPictureIds(rndCat)
        } while (
            previousCategory == rndCat || catPicsIds.isEmpty()
        )
        previousCategory = rndCat!!
        return Pair(rndCat, catPicsIds)
    }

    /**
     * Displays the representative of the given category on the given ImageView
     * @param view The ImageView on which the representative is going to be displayed
     * @param category The category whose representative will be displayed
     */
    suspend fun displayTargetPicture(activity: Activity, view: ImageView, category: Category) {
        dbMgt.getRepresentativePicture(category.id)?.displayOn(activity, view)
    }
}
