package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningMode


/**
 * Presenter for the learning fragment
 * @param dbMgt database manager used to retrieve data for learning
 * @param learningMode learning mode. Influences which pictures are displayed for example.
 * @param dataset dataset used for the learning
 * @param auth The authentication presenter, used to save statistics for the current user
 */
class LearningPresenter(
    private val dbMgt: DatabaseManagement,
    private val learningMode: LearningMode,
    private val dataset: Dataset,
    private val auth: AuthenticationPresenter,
) {

    /**
     * Picks a random picture from the dataset, and displays it on the given view
     * @param activity activity on which the image is going to be displayed
     * @param view The view on which to display the chosen picture. Normally has id R.id.learning_im_to_sort
     */
    suspend fun displayNextPicture(activity: Activity, view: ImageView) {
        val rndCat = getRndCategory()
        val rndCatPicIds = dbMgt.getPictureIds(rndCat)
        val rndCatRepr = dbMgt.getRepresentativePicture(rndCat.id)

        val nextPicture = when (learningMode) {
            LearningMode.REPRESENTATION ->
                if (rndCatPicIds.isEmpty()) {
                    // if there is no picture in the category, use the representative
                    rndCatRepr
                } else {
                    // otherwise, take a random picture in belonging to the category
                    dbMgt.getPicture(rndCatPicIds.random())
                }
            LearningMode.PRESENTATION -> rndCatRepr
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
     * Returns a random category of the dataset
     */

    private fun getRndCategory(): Category = dataset.categories.random()


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
