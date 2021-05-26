package com.github.HumanLearning2021.HumanLearningApp.view.learning

import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

typealias Color = Int

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

    fun setupBlinking() {
        setBGBlinkingStates(sourceState = true, targetsState = false)
        startBlinkingCardViews()
    }

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
        startBlinkingForever(sourceCardView)
        targetCardViews.forEach { startBlinkingForever(it) }
    }

    private fun startBlinkingForever(cardView: CardView) {
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

    fun startIncorrectFeedback(cv: CardView) {
        startFeedback(cv, negativeColor)
    }

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