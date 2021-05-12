package com.github.HumanLearning2021.HumanLearningApp.model.learning

import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset


class LearningModel(private val dataset: Dataset) {

    /**
     * Represents the current category that the user must sort
     */
    private lateinit var currentCategory: Category

    /**
     * Maps the ImageView id displaying the representative to the category it represents
     * A missing value indicates that nothing is displayed
     */
    private val imageViewToCategory: MutableMap<ImageView, Category> = HashMap()

    /**
     * TODO
     */
    fun updateCurrentCategory(): Category {
        currentCategory = getRndCategory()
        return currentCategory
    }

    /**
     * Returns a random category amongst the current target categories
     */
    private fun getRndCategory(): Category = imageViewToCategory.values.random()

    /**
     * Verifies if the currentCategory is equal to the category displayed on the given view
     * @param imageViewId View displaying the target category
     * @return true iff the sorting is correct
     */
    fun isSortingCorrect(imageView: ImageView): Boolean =
        imageViewToCategory.contains(imageView) && imageViewToCategory[imageView] == currentCategory

    /**
     * TODO
     */
    fun updateTargetCategories(targetViews: List<ImageView>): Map<ImageView, Category> {
        imageViewToCategory.clear()
        // choose the categories that are going to be displayed at random
        dataset.categories.shuffled().take(targetViews.size)
            // and update the mapping and display the category
            .forEachIndexed { i, cat ->
                val iv = targetViews[i]
                imageViewToCategory += Pair(iv, cat)
            }
        return imageViewToCategory
    }
}
