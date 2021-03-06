package com.github.HumanLearning2021.HumanLearningApp.view


import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GoogleSignInFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var fragment: GoogleSignInFragment
    private val mockNavController: NavController = Mockito.mock(NavController::class.java)


    @Before
    fun setUp() {
        hiltRule.inject()
        launchFragment()
    }

    @Test
    fun test_success() {
        launchFragmentInHiltContainer<GoogleSignInFragment> {
            @Suppress("DEPRECATION")
            onActivityResult(GoogleSignInFragment.RC_SIGN_IN, 0, null)
        }
    }

    @Test
    fun checkBoxUiTest() {
        onView(withId(R.id.textView_login_status)).check(matches(withText("Not logged in!")))
        onView(withId(R.id.checkBox)).check(matches(not(isChecked())))
        onView(withId(R.id.checkBox)).perform(click()).check(matches(isChecked()))
        onView(withId(R.id.checkBox)).perform(click()).check(matches(not(isChecked())))
    }

    @Test
    fun checkedBoxSetAdminTrue() {
        onView(withId(R.id.checkBox)).perform(click())
        assertThat(fragment.isAdmin, equalTo(true))

    }

    @Test
    fun uncheckedBoxSetAdminFalse() {
        onView(withId(R.id.checkBox)).perform(click())
        onView(withId(R.id.checkBox)).perform(click())
        assertThat(GoogleSignInFragment.isAdmin, equalTo(false))
    }


    @Test
    fun test_signOutIsDisplayed() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            fragment.presenter.onSuccessfulLogin(false)
            fragment.activity?.runOnUiThread {
                fragment.updateUi()
            }
        }
        onView(withId(R.id.button_sign_out)).check(matches((isDisplayed())))
    }


    @Test
    fun signOutUserSuccess() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            fragment.presenter.onSuccessfulLogin(false)
            fragment.activity?.runOnUiThread {
                fragment.updateUi()
                fragment.onSignOutPress()
                assertThat(fragment.presenter.currentUser, Matchers.nullValue())
            }
        }
        onView(withId(R.id.button_login)).check(matches(isDisplayed()))
        onView(withId(R.id.checkBox)).check(matches(isDisplayed()))
        onView(withId(R.id.textView_login_status)).check(matches(withText("Not logged in!")))
    }

    @Test
    fun testLoginPersistence() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            fragment.presenter.onSuccessfulLogin(false)
            fragment.handleSignIn()
            assertThat(fragment.prefs.getBoolean("hasLogin", false), equalTo(true))
            assertThat(fragment.prefs.getBoolean("isAdmin", false), equalTo(false))
            fragment.activity?.runOnUiThread {
                fragment.onSignOutPress()
                assertThat(fragment.prefs.getBoolean("hasLogin", false), equalTo(false))
                assertThat(fragment.prefs.getString("name", ""), equalTo(""))
                assertThat(fragment.prefs.getString("email", ""), equalTo(""))
                assertThat(fragment.prefs.getString("uid", ""), equalTo(""))
            }
        }
    }

    @Test
    fun test_setSignInUI() {
        fragment.setSignInUi()
        onView(withId(R.id.button_login)).check(matches(isDisplayed()))
        onView(withId(R.id.checkBox)).check(matches(isDisplayed()))
    }

    @Test
    fun test_setSignOutUI() {
        fragment.activity?.runOnUiThread {
            fragment.setSignOutUi()
        }
        onView(withId(R.id.button_sign_out)).check(matches((isDisplayed())))
    }

    private fun launchFragment() {
        launchFragmentInHiltContainer<GoogleSignInFragment> {
            fragment = this
            Navigation.setViewNavController(requireView(), mockNavController)
        }
    }
}



