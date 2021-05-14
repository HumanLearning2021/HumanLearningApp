package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.*
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
    private val imageDisplayer: ImageDisplayer,
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
            with(imageDisplayer) {
                dbMgt.getRepresentativePicture(cat.id)?.displayOn(iv)
            }
            // set new content description. ONLY FOR ACCESSIBILITY REASONS, NOT FOR FUNCTIONALITY
            iv.contentDescription = cat.name
        }

        // It matters that this method is called last, because the picture to sort must have a
        // category amongst those that are displayed
        updatePictureToSort(activity, sourceView)
    }

    private var previousCategory: Category? = null

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
            LearningMode.EVALUATION -> rndCatRepr // TODO adapt
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
        with(imageDisplayer) {
            dbMgt.getRepresentativePicture(category.id)?.displayOn(view)
        }
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
