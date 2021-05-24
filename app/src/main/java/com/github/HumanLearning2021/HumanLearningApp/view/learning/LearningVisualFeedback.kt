package com.github.HumanLearning2021.HumanLearningApp.view.learning

import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

typealias Color = Int

class LearningVisualFeedback(
    private val lifecycleScope: LifecycleCoroutineScope,
    private val neutralColor: Color,
    private val positiveColor: Color,
    private val sourceCardView: CardView,
    private val targetCardViews: List<CardView>
) {
    private val cardViewBGShouldBlink: MutableMap<CardView, Boolean> = HashMap()

    fun setupBlinking() {
        setBGBlinkingStates(sourceState = true, targetsState = false)
        initCardViewBGBlinking()
    }

    fun setBGBlinkingStates(
        sourceState: Boolean,
        targetsState: Boolean
    ) {
        cardViewBGShouldBlink[sourceCardView] = sourceState
        for (cv in targetCardViews) {
            cardViewBGShouldBlink[cv] = targetsState
        }
    }

    private fun initCardViewBGBlinking() {
        startBlinking(sourceCardView)
        targetCardViews.forEach { startBlinking(it) }
    }

    private fun startBlinking(cardView: CardView) {
        lifecycleScope.launch {
            blinkCardViewBGColor(cardView)
        }
    }

    private tailrec suspend fun blinkCardViewBGColor(
        cardView: CardView,
        nextColor: Color = positiveColor
    ) {
        if (cardViewBGShouldBlink[cardView] == true) {
            cardView.setCardBackgroundColor(nextColor)
        } else {
            cardView.setCardBackgroundColor(neutralColor)
        }
        delay(BLINK_DURATION_MS)
        when (nextColor) {
            positiveColor -> blinkCardViewBGColor(cardView, neutralColor)
            else -> blinkCardViewBGColor(cardView, positiveColor)
        }
    }

    companion object {
        private val BLINK_DURATION_MS: Long = 600
    }

}