package com.github.HumanLearning2021.HumanLearningApp.presenter

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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
    fun test_signInAnonymouslyUser() = runBlocking {
        val firebaseUser = Firebase.auth.signInAnonymously().await().user!!
        presenter.onSuccessfulLogin(false)
        val user = presenter.currentUser
        assertThat(user, notNullValue())
        user!!
        assertThat(user.type, equalTo(User.Type.FIREBASE))
        assertThat(user.uid, equalTo(firebaseUser.uid))
        assertThat(user.isAdmin, equalTo(false))
    }

    @Test
    fun test_signInAnonymouslyAdmin() = runBlocking {
        val firebaseUser = Firebase.auth.signInAnonymously().await().user!!
        presenter.onSuccessfulLogin(true)
        val user = presenter.currentUser
        assertThat(user, notNullValue())
        user!!
        assertThat(user.type, equalTo(User.Type.FIREBASE))
        assertThat(user.uid, equalTo(firebaseUser.uid))
        assertThat(user.isAdmin, equalTo(true))
    }

    @Test
    fun signOutUserSuccess() {
        runBlocking {
            val firebaseUser = Firebase.auth.signInAnonymously().await().user!!
            presenter.onSuccessfulLogin(true)
            presenter.signOut()
            val user = presenter.currentUser

        }

    }

}