package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.util.Log
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.model.learning.LearningModel
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Presenter for the learning fragment
 * @param dbMgt database manager used to retrieve data for learning
 * @param learningMode learning mode. Influences which pictures are displayed for example.
 * @param dataset dataset used for the learning
 * @param auth The authentication presenter, used to save statistics for the current user
 * @param imageDisplayer handles image displaying
 * @param coroutineScope allows to launch coroutines (for image displaying for example)
 */
class LearningPresenter(
    private val dbMgt: DatabaseManagement,
    private val learningMode: LearningMode,
    private val dataset: Dataset,
    private val auth: AuthenticationPresenter,
    private val imageDisplayer: ImageDisplayer,
    private val coroutineScope: CoroutineScope
) {

    /**
     * Underlying model for the learning
     */
    val learningModel = LearningModel(dataset)


    /**
     * Updates the model and the UI so that it is ready for the next sorting
     * @param targetViews the views that display the target categories
     * @param sourceView the view that displays the current picture to sort
     */
    fun updateForNextSorting(
        targetViews: List<ImageView>,
        sourceView: ImageView
    ) {
        require(dataset.categories.size >= targetViews.size) {
            "There must be enough categories in the dataset to display one category on each target" +
                    " ImageView"
        }

        learningModel.updateForNextSorting(targetViews).forEach {
            val (iv, cat) = it
            with(imageDisplayer) {
                coroutineScope.launch {
                    dbMgt.getRepresentativePicture(cat.id)?.displayOn(iv)
                }
            }
            // set new content description. ONLY FOR ACCESSIBILITY REASONS, NOT FOR FUNCTIONALITY
            iv.contentDescription = cat.name
        }

        // It matters that this method is called last, because the picture to sort must have a
        // category amongst those that are displayed
        coroutineScope.launch {
            updatePictureToSort(sourceView)
        }
    }

    /**
     * Picks a random picture from the dataset, and displays it on the given view
     * @param view The view on which to display the chosen picture. Normally has id R.id.learning_im_to_sort
     */
    private suspend fun updatePictureToSort(view: ImageView) {
        val currentCategory = learningModel.getCurrentCategory()
        val picIdsForCategory = dbMgt.getPictureIds(currentCategory)
        val categoryReprPic = dbMgt.getRepresentativePicture(currentCategory.id)
        val rndPicForCategory = if (picIdsForCategory.isEmpty()) {
            // if there is no picture in the category, use the representative
            categoryReprPic
        } else {
            // otherwise, take a random picture in belonging to the category
            dbMgt.getPicture(picIdsForCategory.random())
        }
        val nextPicture = when (learningMode) {
            LearningMode.PRESENTATION -> categoryReprPic
            LearningMode.REPRESENTATION -> rndPicForCategory
            LearningMode.EVALUATION -> rndPicForCategory
        }

        if (nextPicture != null) {
            with(imageDisplayer) {
                nextPicture.displayOn(view)
            }
        } else {
            Log.e(this::class.java.name, "There is no next picture to display")
        }
        // contentDescription only used for accessibility reasons
        view.contentDescription = currentCategory.name
        view.invalidate()
    }

    /**
     * Saves an event for use in statistics
     * @param event to save
     */
    suspend fun saveEvent(event: Event) =
        auth.currentUser?.let { user ->
            dbMgt.countOccurrence(user.id, dataset.id, event)
        }

    /**
     * @see LearningModel.isSortingCorrect for documentation
     */
    fun isSortingCorrect(v: ImageView) = learningModel.isSortingCorrect(v)
}
