package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.Event
import com.github.HumanLearning2021.HumanLearningApp.model.id
import com.github.HumanLearning2021.HumanLearningApp.model.learning.LearningModel
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
     * Underlying model for the learning
     */
    val learningModel = LearningModel(dataset)


    /**
     * Updates the model and the UI so that it is ready for the next sorting
     * @param activity parent activity of the given views
     * @param targetViews the views that display the target categories
     * @param sourceView the view that displays the current picture to sort
     */
    suspend fun updateForNextSorting(
        activity: Activity,
        targetViews: List<ImageView>,
        sourceView: ImageView
    ) {
        require(dataset.categories.size >= targetViews.size) {
            "There must be enough categories in the dataset to display one category on each target" +
                    " ImageView"
        }

        learningModel.updateForNextSorting(targetViews).forEach {
            val (iv, cat) = it
            dbMgt.getRepresentativePicture(cat.id)?.displayOn(activity, iv)
            // set new content description. ONLY FOR ACCESSIBILITY REASONS, NOT FOR FUNCTIONALITY
            iv.contentDescription = cat.name
        }

        // It matters that this method is called last, because the picture to sort must have a
        // category amongst those that are displayed
        updatePictureToSort(activity, sourceView)
    }

    /**
     * Picks a random picture from the dataset, and displays it on the given view
     * @param activity activity on which the image is going to be displayed
     * @param view The view on which to display the chosen picture. Normally has id R.id.learning_im_to_sort
     */
    private suspend fun updatePictureToSort(activity: Activity, view: ImageView) {
        val currentCategory = learningModel.getCurrentCategory()
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
        // contentDescription only used for accessibility reasons
        view.contentDescription = currentCategory.name
        view.invalidate()
    }

    suspend fun saveEvent(event: Event) =
        auth.currentUser?.let { user ->
            dbMgt.countOccurrence(user.id, dataset.id, event)
        }

    /**
     * @see LearningModel.isSortingCorrect for documentation
     */
    fun isSortingCorrect(v: ImageView) = learningModel.isSortingCorrect(v)
}
