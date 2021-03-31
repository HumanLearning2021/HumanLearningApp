package com.github.HumanLearning2021.HumanLearningApp.presenter

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthenticationPresenterTest {
    private lateinit var presenter: AuthenticationPresenter

    @Before
    fun setUp() {
        presenter = AuthenticationPresenter(AuthUI.getInstance(), DummyDatabaseService())
    }
    @Test
    fun test_currentUser() {
        assertThat(presenter.currentUser, nullValue())
    }

    @Test
    fun test_signInAnonymously() = runBlocking {
        val firebaseUser = Firebase.auth.signInAnonymously().await().user!!
        presenter.onSuccessfulLogin()
        val user = presenter.currentUser
        assertThat(user, notNullValue())
        user!!
        assertThat(user.type, equalTo(User.Type.FIREBASE))
        assertThat(user.uid, equalTo(firebaseUser.uid))
    }

}