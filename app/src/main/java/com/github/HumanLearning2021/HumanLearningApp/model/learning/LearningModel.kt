package com.github.HumanLearning2021.HumanLearningApp.model.learning

import android.util.Log
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset

/**
 * This class models the learning process.
 * @param dataset dataset with which the learning happens
 */
class LearningModel(private val dataset: Dataset) {

    /**
     * Represents the category of the current picture to sort
     */
    private lateinit var currentCategory: Category

    /**
     * Get the category of the current picture to sort
     */
    fun getCurrentCategory(): Category {
        return currentCategory
    }

    /**
     * Maps the ImageView id displaying the representative to the category it represents
     * A missing value indicates that nothing is displayed
     */
    private val imageViewToCategory: MutableMap<ImageView, Category> = HashMap()

    /**
     * Verifies if the currentCategory is equal to the category displayed on the given view
     * @param imageView View displaying the target category
     * @return true iff the sorting is correct
     */
    fun isSortingCorrect(imageView: ImageView): Boolean {
        Log.d(
            "isSortingCorrect", "\n\tcurrent category: $currentCategory" +
                    "\ndropped on target category: ${imageViewToCategory[imageView]}"
        )
        return imageViewToCategory.contains(imageView) &&
                imageViewToCategory[imageView] == currentCategory
    }

    /**
     * Update the model to be ready for the next sorting.
     * In particular, choose the new target categories and the new category of the picture to sort
     * @param targetViews image views used to display target categories
     * @return the newly updated mapping from ImageView to corresponding category
     */
    fun updateForNextSorting(targetViews: List<ImageView>): Map<ImageView, Category> {
        imageViewToCategory.clear()
        // choose the categories that are going to be displayed at random
        dataset.categories.shuffled().take(targetViews.size)
            // and update the mapping and display the category
            .forEachIndexed { i, cat ->
                val iv = targetViews[i]
                imageViewToCategory += Pair(iv, cat)
            }
        // update the current category of the picture to sort
        currentCategory = imageViewToCategory.values.random()
        return imageViewToCategory
    }
}
