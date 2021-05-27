package com.github.HumanLearning2021.HumanLearningApp.view.learning

import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

typealias Color = Int

/**
 * This class is tasked with the logic that adds visual feedback for the learning fragment
 * (for example blinking the border in green on correct classification).
 * @property lifecycleScope allows launching coroutines for visual feedback
 * @property baseColor color that represents the color of elements before they are affected by
 * the visual feedback. eg. this is white for the background of a CardView
 * @property neutralColor color different from the baseColor that has no connotation (eg. blue).
 * Will be used to indicate elements on screen
 * @property positiveColor color that indicates a positive event, for example a correct sorting.
 * @property negativeColor color that indicates a negative event, for example an incorrect sorting
 * @property sourceCardView CardView in which the image to sort (source image) is contained
 * @property targetCardViews CardViews that contain the representatives of the target categories
 */
class LearningVisualFeedback(
    private val lifecycleScope: LifecycleCoroutineScope,
    private val baseColor: Color,
    private val neutralColor: Color,
    private val positiveColor: Color,
    private val negativeColor: Color,
    private val sourceCardView: CardView,
    private val targetCardViews: List<CardView>
) {
    private var sourceCardViewShouldBlink = false

    init {
        // this thread runs until the fragment is destroyed, and makes the source CardView blink
        // only when sourceCardViewShouldBlink is true
        lifecycleScope.launchWhenResumed {
            while (true) {
                if (sourceCardViewShouldBlink) {
                    blink(sourceCardView, baseColor, neutralColor, BLINK_DURATION_MS)
                } else {
                    // make less aggressive busy wait
                    delay(BLINK_DURATION_MS)
                }
            }
        }
    }

    /**
     * Starts blinking rapidly with the positiveColor to indicate a success.
     * @param cv the CardView whose background will blink
     */
    fun startCorrectFeedback(cv: CardView) {
        startFeedback(cv, positiveColor)
    }


    /**
     * Starts blinking rapidly with the negativeColor to indicate a mistake.
     * @param cv the CardView whose background will blink
     */
    fun startIncorrectFeedback(cv: CardView) {
        startFeedback(cv, negativeColor)
    }


    /**
     * Sets whether the background of the source view should blink
     * @param v the new value. True indicates that it should blink
     */
    fun sourceCardViewShouldBlink(v: Boolean) {
        sourceCardViewShouldBlink = v
    }


    /**
     * This method should be called upon events that are considered as the start of a drag action.
     * For example if the source image is touched.
     */
    fun dragStarted() {
        setTargetsBGTo(neutralColor)
        sourceCardViewShouldBlink(false)
    }


    /**
     * This method should be called upon events that are considered as the end of a drag action.
     * For example if the source image is dropped on a category, or if it is release without being
     * dropped.
     */
    fun dragEnded() {
        setTargetsBGTo(baseColor)
        sourceCardViewShouldBlink(true)
    }


    private fun setTargetsBGTo(color: Color) {
        targetCardViews.forEach {
            it.setCardBackgroundColor(color)
        }
    }


    private suspend fun blink(
        cardView: CardView,
        baseColor: Color,
        blinkColor: Color,
        delayMs: Long
    ) {
        cardView.setCardBackgroundColor(baseColor)
        delay(delayMs)
        cardView.setCardBackgroundColor(blinkColor)
        delay(delayMs)
        cardView.setCardBackgroundColor(baseColor)
    }


    private fun startFeedback(cv: CardView, blinkColor: Color) {
        lifecycleScope.launch {
            for (i in 1..NB_BLINKS_FOR_SORTING_FEEDBACK) {
                blink(cv, baseColor, blinkColor, SHORT_BLINK_DURATION_MS)
            }
        }
    }


    companion object {
        private val BLINK_DURATION_MS: Long = 800
        private val SHORT_BLINK_DURATION_MS: Long = 150
        private val NB_BLINKS_FOR_SORTING_FEEDBACK = 4
    }
}