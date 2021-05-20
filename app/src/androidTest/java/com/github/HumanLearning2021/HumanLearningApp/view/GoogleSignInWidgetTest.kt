package com.github.HumanLearning2021.HumanLearningApp.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2CachePictureRepository
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.offline.PictureRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GoogleSignInWidgetTest {

    @Inject
    @Demo2Database
    lateinit var demo2DbService: DatabaseService

    @Inject
    @Demo2CachePictureRepository
    lateinit var repository: PictureRepository

    @BindValue
    @Demo2Database
    lateinit var demo2DbMgt: DatabaseManagement

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var fragment: GoogleSignInWidget

    @Before
    fun setUp() {
        hiltRule.inject()
        demo2DbMgt = DatabaseManagementModule.provideDemo2Service(demo2DbService)
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

    private fun launchFragment() {
        launchFragmentInHiltContainer<GoogleSignInWidget> {
            fragment = this
        }
    }

}



