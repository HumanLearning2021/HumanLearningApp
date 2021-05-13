package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningMode
import javax.inject.Inject

class LearningPresenter @Inject constructor(
    @Demo2Database
    private val dbMgt: DatabaseManagement,
    private val auth: AuthenticationPresenter,
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

        if (nextPicture != null) {
            nextPicture.displayOn(activity, view)
        } else {
            Log.e(this::class.java.name, "There is no next picture to display")
        }
        view.contentDescription = rndCat.name
        view.invalidate()
    }

    /**
     * Returns a random category of the dataset, with the corresponding list of picture *ids*.
     * The returned category is guaranteed to be different from the previousCategory
     * The list of picture ids is guaranteed to ben non-empty
     */
    private suspend fun getRndCategoryWithPictureIds(): Pair<Category, List<Id>> {
        var rndCat: Category?
        var catPicsIds: List<Id>
        do {
            rndCat = dataset.categories.random()
            catPicsIds = dbMgt.getPictureIds(rndCat)
        } while (
        // TODO(Niels) : risk of infinite loop if 2 categories in dataset or no category
        //  with a picture. To fix
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

    suspend fun saveEvent(event: Event) =
        auth.currentUser?.let { user ->
            dbMgt.countOccurrence(user.id, dataset.id, event)
        }
}
