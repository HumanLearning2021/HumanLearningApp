package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import javax.inject.Inject

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SelectPictureActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    @RoomDatabase
    lateinit var room: RoomOfflineDatabase

    @BindValue
    @Demo2Database
    lateinit var dbManagement: DatabaseManagement

    private val datasetId: Id = TestUtils.getFirstDataset(dbManagement).id

    private val catSet = setOf<Category>(
        DummyCategory("cat1", "cat1"),
        DummyCategory("cat2", "cat2"),
        DummyCategory("cat3", "cat3"),
    )

    private val navController: NavController = Mockito.mock(NavController::class.java)


    @Before
    fun setup() {
        hiltRule.inject()
        dbManagement = DefaultDatabaseManagement(DummyDatabaseService(), "dummy", context, room)
        launchFragment()
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun correctLayoutIsDisplayAfterCreation() {
        BaristaVisibilityAssertions.assertDisplayed(R.id.choosePictureButton)
        BaristaVisibilityAssertions.assertDisplayed(R.id.selectCategoryButton2)
        BaristaVisibilityAssertions.assertDisplayed(R.id.saveButton3)
    }

    @Test
    fun categoriesCorrectlySetAfterCreation() {
        onView(withId(R.id.selectCategoryButton2)).perform(click())
        onView(withText("cat1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText("cat2"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText("cat3"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun correctNavigationOnSave() {
        val imageUri =
            Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.knife)
        onView(withId(R.id.selectCategoryButton2)).perform(click())
        onView(withText("cat1")).perform(click())
        intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(
            Intent().run {
                data = imageUri
                Instrumentation.ActivityResult(SelectPictureFragment.RC_OPEN_PICTURE, this)
            })
        onView(withId(R.id.choosePictureButton)).perform(click())
        onView(withId(R.id.saveButton3)).perform(click())
        verify(navController).popBackStack()
    }

    @Test
    fun backButtonWorks() {
        Espresso.pressBack()
        verify(navController).popBackStack()
    }

    private fun launchFragment() {
        val args = bundleOf("categories" to catSet.toTypedArray(), "datasetId" to datasetId)
        launchFragmentInHiltContainer<SelectPictureFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}
