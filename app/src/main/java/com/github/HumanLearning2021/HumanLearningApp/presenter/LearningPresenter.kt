package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningMode


/**
 * Id of a view in the layout. Allows to clarify targetCategories map in LearningPresenter
 */
typealias ViewId = Int

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
     * Represents the current category that the user must sort
     */
    private lateinit var currentCategory: Category

    /**
     * Maps the ImageView id displaying the representative to the category it represents
     * A missing value indicates that nothing is displayed
     */
    private val targetCategories: MutableMap<ViewId, Category> = HashMap()


    /**
     * Update the target categories for the learning. This method chooses n categories at random in
     * the dataset (n = targetViews.size) and displays them on the given targetViews
     * @param activity activity on which the targetViews appear
     * @param targetViews ImageViews on which the new category representatives are going to be displayed
     */
    suspend fun updateTargetCategories(activity: Activity, targetViews: List<ImageView>) {
        val categoriesInDataset = dataset.categories
        require(categoriesInDataset.size >= targetViews.size) {
            "There must be enough categories in the dataset to display one category on each target" +
                    " ImageView"
        }
        // choose the categories that are going to be displayed at random
        categoriesInDataset.shuffled().take(targetViews.size)
            // and update the mapping and display the category
            .forEachIndexed { i, cat ->
                val iv = targetViews[i]
                targetCategories += Pair(iv.id, cat)
                dbMgt.getRepresentativePicture(cat.id)?.displayOn(activity, iv)
                // set new content description. ONLY FOR ACCESSIBILITY REASONS, NOT FOR FUNCTIONALITY
                iv.contentDescription = cat.name
            }
    }

    /**
     * Picks a random picture from the dataset, and displays it on the given view
     * @param activity activity on which the image is going to be displayed
     * @param view The view on which to display the chosen picture. Normally has id R.id.learning_im_to_sort
     */
    suspend fun updateCurrentCategory(activity: Activity, view: ImageView) {
        currentCategory = getRndCategory()
        val rndCatPicIds = dbMgt.getPictureIds(currentCategory)
        val rndCatRepr = dbMgt.getRepresentativePicture(currentCategory.id)

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
        view.contentDescription = currentCategory.name
        view.invalidate()
    }

    /**
     * Returns a random category of the dataset
     */

    private fun getRndCategory(): Category = dataset.categories.random()


    fun isSortingCorrect(v: View): Boolean {
        TODO(
            "function that returns wether the sorting was correct, according to the view" +
                    " representing the target category"
        )

    }

    suspend fun saveEvent(event: Event) =
        auth.currentUser?.let { user ->
            dbMgt.countOccurrence(user.id, dataset.id, event)
        }
}
