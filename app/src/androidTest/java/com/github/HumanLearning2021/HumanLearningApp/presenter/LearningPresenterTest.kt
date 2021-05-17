package com.github.HumanLearning2021.HumanLearningApp.presenter

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.Event
import com.github.HumanLearning2021.HumanLearningApp.model.id
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningMode
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LearningPresenterTest {
    val db = DummyDatabaseService()
    val dbMgt = DefaultDatabaseManagement(db)
    val authPres = AuthenticationPresenter(AuthUI.getInstance(), db)
    val dataset = runBlocking { db.putDataset("Stuff'n'things", setOf()) }
    val presenter = LearningPresenter(
        dbMgt, LearningMode.PRESENTATION,
        dataset,
        authPres
    )

    @Before
    fun setUp() {
        runBlocking {
            Firebase.auth.signInAnonymously().await()
            authPres.onSuccessfulLogin()
        }
    }

    @Test
    fun saveEvent() {
        val event = Event.MISTAKE
        val countBefore = runBlocking {
            db.getStatistic(
                authPres.currentUser!!.id,
                dataset.id
            )?.occurrences?.get(event) ?: 0
        }
        runBlocking {
            presenter.saveEvent(event)
        }
        val countAfter = runBlocking {
            db.getStatistic(authPres.currentUser!!.id, dataset.id)!!.occurrences[event]
        }
        assertThat(countAfter, equalTo(countBefore + 1))
    }
}