package com.github.HumanLearning2021.HumanLearningApp.model

import com.github.HumanLearning2021.HumanLearningApp.model.learning.EvaluationModel
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class EvaluationModelTest {
    private var catUniqueId = 0
    private fun makeMockDataset(name: String, nbCat: Int) = Dataset(
        name,
        name,
        (0 until nbCat).map
        { Category("${catUniqueId++}", "name") }.toSet()
    )

    @Test
    fun testInitialValuesOfModel() {
        val nbCat = 42
        val m = EvaluationModel(makeMockDataset("bla", nbCat))
        // the result should have as many entries as the number of phases, and thus categories
        assertThat(
            m.getCurrentEvaluationResult().successFailureCountPerPhase,
            hasSize(nbCat + 1)
        )
        // assert all counts start at 0
        assertThat(
            m.getCurrentEvaluationResult().successFailureCountPerPhase,
            everyItem(equalTo(0 to 0))
        )

        // evaluation should not start completed
        assertThat(m.isEvaluationComplete(), equalTo(false))

        // start in phase 1
        assertThat(m.getCurrentPhase(), equalTo(1))
    }

    @Test
    fun allCorrectClassificationsCompletesEvaluation() {
        val nbCat = 38
        val m = EvaluationModel(makeMockDataset("bip", nbCat))
        val upper = nbCat * EvaluationModel.STREAK_LENGTH - 1
        for (i in 1..upper) {
            m.addSuccess()
        }
        // we are one success away from completion
        assertThat(m.isEvaluationComplete(), equalTo(false))
        m.addSuccess()
        assertThat(m.isEvaluationComplete(), equalTo(true))

        assertThat(
            // drop 1 because (0,0) at index 0 (there's no phase 0)
            m.getCurrentEvaluationResult().successFailureCountPerPhase.drop(1),
            everyItem(equalTo(EvaluationModel.STREAK_LENGTH to 0))
        )
    }

    @Test
    fun failuresDontIncreasePhase() {
        val m = EvaluationModel(makeMockDataset("blip", 2))
        val startPhase = m.getCurrentPhase()
        val nbFailures = 49
        for (i in 1..nbFailures) {
            m.addFailure()
        }
        assertThat(m.isEvaluationComplete(), equalTo(false))
        assertThat(
            m.getCurrentEvaluationResult().successFailureCountPerPhase[startPhase],
            equalTo(0 to nbFailures)
        )
        assertThat(m.getCurrentPhase(), equalTo(startPhase))
    }
}
