package com.github.HumanLearning2021.HumanLearningApp.model.learning

import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import java.util.*

class EvaluationModel(private val dataset: Dataset) {
    /**
     * Phase number corresponds to nb of target categories on which to sort
     * Start with 1 category
     */
    private var currentPhase = 1

    private var nbSuccessesInStreak = 0
    private var nbFailuresInStreak = 0
    private fun nbAttemptsInStreak() = nbSuccessesInStreak + nbFailuresInStreak
    private fun successRateInStreak() = nbSuccessesInStreak / nbAttemptsInStreak()
    private val STREAK_LENGTH = 10

    private var MIN_SUCCESS_RATE_FOR_NEXT_PHASE = .8

    private var evaluationComplete = false

    /**
     * List indexed by phase number, values represent
     * (number of successes in phase, number of failures in phase)
     */
    private val countPerPhase = ArrayList<Pair<Int, Int>>()

    private fun countForCurrentPhase() = countPerPhase.getOrElse(currentPhase) { 0 to 0 }

    fun addSuccess() {
        nbSuccessesInStreak++
        val (nbSuccesses, nbFailures) = countForCurrentPhase()
        countPerPhase[currentPhase] = (nbSuccesses + 1) to nbFailures
        checkIfPhaseOrEvaluationComplete()
    }

    fun addFailure() {
        nbFailuresInStreak++
        val (nbSuccesses, nbFailures) = countForCurrentPhase()
        countPerPhase[currentPhase] = nbSuccesses to (nbFailures + 1)
        checkIfPhaseOrEvaluationComplete()
    }

    private fun checkIfPhaseOrEvaluationComplete() {
        if (nbAttemptsInStreak() == STREAK_LENGTH) {
            if (successRateInStreak() >= MIN_SUCCESS_RATE_FOR_NEXT_PHASE) {
                if (currentPhase >= dataset.categories.size) {
                    evaluationComplete = true
                } else {
                    currentPhase++
                }
            } else {
                nbSuccessesInStreak = 0
                nbFailuresInStreak = 0
            }
        }
    }
}