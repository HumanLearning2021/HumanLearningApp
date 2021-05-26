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
    private val cardViewBGShouldBlink: MutableMap<CardView, Boolean> = HashMap()

    /**
     * Starts blinking steadily the CardView behind the source image with the neutralColor.
     * Also does the setup that will allow the target images backgrounds to blink.
     */
    fun setupBlinkingForHints() {
        setBGBlinkingStates(sourceState = true, targetsState = false)
        startBlinkingCardViews()
    }

    /**
     * Sets the blinking states for visual hints.
     * @param sourceState state of blinking for the source image.
     * True indicates that it should blink
     * @param targetsState state of blinking for the target images.
     * True indicates that they should blink
     */
    fun setBGBlinkingStates(
        sourceState: Boolean,
        targetsState: Boolean
    ) {
        cardViewBGShouldBlink[sourceCardView] = sourceState
        for (cv in targetCardViews) {
            cardViewBGShouldBlink[cv] = targetsState
        }
        startBlinkingCardViews()
    }


    private fun startBlinkingCardViews() {
        startBlinkingUntilInterrupted(sourceCardView)
        targetCardViews.forEach { startBlinkingUntilInterrupted(it) }
    }

    private fun startBlinkingUntilInterrupted(cardView: CardView) {
        lifecycleScope.launch {
            blinkCardViewBGColor(cardView)
        }
    }


    private suspend fun blinkCardViewBGColor(cardView: CardView) {
        while (cardViewBGShouldBlink[cardView] == true) {
            blink(cardView, baseColor, neutralColor, BLINK_DURATION_MS)
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

    /**
     * Starts blinking rapidly with the negativeColor to indicate a mistake.
     * @param cv the CardView whose background will blink
     */
    fun startIncorrectFeedback(cv: CardView) {
        startFeedback(cv, negativeColor)
    }

    /**
     * Starts blinking rapidly with the positiveColor to indicate a success.
     * @param cv the CardView whose background will blink
     */
    fun startCorrectFeedback(cv: CardView) {
        startFeedback(cv, positiveColor)
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