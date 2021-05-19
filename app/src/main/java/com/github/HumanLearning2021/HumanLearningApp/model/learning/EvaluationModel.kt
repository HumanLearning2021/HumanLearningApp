package com.github.HumanLearning2021.HumanLearningApp.model.learning

import android.os.Parcelable
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Represents the result of an evaluation.
 * @param successFailureCountPerPhase maps each phase number to a tuple (#successes, #failures)
 * For example, if the value at index 3 is (17, 13), this means that the learner made 17 successful
 * sortings, and 13 unsuccessful sortings for the phase that uses 3 categories of the dataset.
 */
@Parcelize
data class EvaluationResult(val successFailureCountPerPhase: List<Pair<Int, Int>>) : Parcelable


class EvaluationModel(private val dataset: Dataset) {
    /**
     * Phase number corresponds to nb of target categories on which to sort
     * Start with 1 category
     */
    private var currentPhase = 1

    fun getCurrentPhase(): Int = currentPhase

    private var nbSuccessesInStreak = 0
    private var nbFailuresInStreak = 0
    private fun nbAttemptsInStreak() = nbSuccessesInStreak + nbFailuresInStreak
    private fun successRateInStreak() = nbSuccessesInStreak / nbAttemptsInStreak()

    /**
     * One streak consists in STREAK_LENGTH attempts at sorting a picture
     *
     * Ideally, this should by configurable in LearningSettings
     */
    private val STREAK_LENGTH = 10

    /**
     * At the end of each streak, we check
     * if the success rate was above or equal to MIN_SUCCESS_RATE_FOR_NEXT_PHASE.
     * If it is the case, we go to the next phase, otherwise we reset the streak
     *
     * Ideally, this should by configurable in LearningSettings
     */
    private var MIN_SUCCESS_RATE_FOR_NEXT_PHASE = .8

    /**
     * This flag indicates whether the evaluation is complete
     */
    private var evaluationComplete = false

    /**
     * List indexed by phase number, values represent
     * (number of successes in phase, number of failures in phase)
     */
    private val successFailureCountPerPhase = ArrayList<Pair<Int, Int>>()

    /**
     * Get the evaluation result for the current state of the model.
     * @see EvaluationResult
     */
    fun getCurrentEvaluationResult() = EvaluationResult(successFailureCountPerPhase)

    private fun successFailureCountForCurrentPhase() =
        successFailureCountPerPhase.getOrElse(currentPhase) { 0 to 0 }

    /**
     * Add a success to the success count in this streak & phase.
     * Also checks if the phase or the evaluation is complete
     */
    fun addSuccess() {
        nbSuccessesInStreak++
        val (nbSuccesses, nbFailures) = successFailureCountForCurrentPhase()
        successFailureCountPerPhase[currentPhase] = (nbSuccesses + 1) to nbFailures
        checkIfPhaseOrEvaluationComplete()
    }

    /**
     * Add a failure to the failure count in this streak & phase.
     * Also checks if the phase or the evaluation is complete
     */
    fun addFailure() {
        nbFailuresInStreak++
        val (nbSuccesses, nbFailures) = successFailureCountForCurrentPhase()
        successFailureCountPerPhase[currentPhase] = nbSuccesses to (nbFailures + 1)
        checkIfPhaseOrEvaluationComplete()
    }

    private fun checkIfPhaseOrEvaluationComplete() {
        if (nbAttemptsInStreak() >= STREAK_LENGTH) {
            if (successRateInStreak() >= MIN_SUCCESS_RATE_FOR_NEXT_PHASE) {
                if (currentPhase >= dataset.categories.size) {
                    evaluationComplete = true
                } else {
                    currentPhase++
                }
            }

            resetStreak()
        }
    }

    private fun resetStreak() {
        nbSuccessesInStreak = 0
        nbFailuresInStreak = 0
    }
}