package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitForAction
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.exitProcess

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GoogleSignInWidgetTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var fragment: GoogleSignInWidget


    @Before
    fun setUp() {
        hiltRule.inject()

    }



    @Test
    fun test_success() {
        launchFragmentInHiltContainer<GoogleSignInWidget> {
            onActivityResult(GoogleSignInWidget.RC_SIGN_IN, 0, null)
        }
    }

    @Test
    fun checkBoxUiTest() {
        launchFragment()
        onView(withId(R.id.loginStatus)).check(matches(withText("Not logged in!")))
        onView(withId(R.id.checkBox)).check(matches(not(isChecked())))
        onView(withId(R.id.checkBox)).perform(click()).check(matches(isChecked()))
        onView(withId(R.id.checkBox)).perform(click()).check(matches(not(isChecked())))
    }

    @Test
    fun checkedBoxSetAdminTrue() {
        launchFragment()
        onView(withId(R.id.checkBox)).perform(click())
        assertThat(fragment.isAdmin, equalTo(true))

    }

    @Test
    fun uncheckedBoxSetAdminfalse() {
        launchFragment()
        onView(withId(R.id.checkBox)).perform(click())
        onView(withId(R.id.checkBox)).perform(click())
        assertThat(GoogleSignInWidget.isAdmin, equalTo(false))
    }


    @Test
    fun test_signOutIsDisplayed() {

        runBlocking {
            launchFragment()
            val firebaseUser = Firebase.auth.signInAnonymously().await().user!!
            fragment.presenter.onSuccessfulLogin(false)
            fragment.activity?.runOnUiThread {
                fragment.updateUi()
                onView(withId(R.id.singOutButton)).check(matches((isDisplayed())))
            }
        }
    }




    private fun launchFragment() {
        launchFragmentInHiltContainer<GoogleSignInWidget> {
            fragment = this


        }

    }

}



