package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
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
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
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

@UninstallModules(DatabaseNameModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SelectPictureActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @BindValue
    @ProductionDatabaseName
    val dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

    lateinit var datasetId: Id

    private val catSet = setOf(
        Category("cat1", "cat1"),
        Category("cat2", "cat2"),
        Category("cat3", "cat3"),
    )

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        datasetId = TestUtils.getFirstDataset(dbMgt).id
        launchFragment()
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun correctLayoutIsDisplayAfterCreation() {
        BaristaVisibilityAssertions.assertDisplayed(R.id.button_choose_picture)
        BaristaVisibilityAssertions.assertDisplayed(R.id.button_select_category_select_picture_fragment)
        BaristaVisibilityAssertions.assertDisplayed(R.id.button_save_select_picture_fragment)
    }

    @Test
    fun categoriesCorrectlySetAfterCreation() {
        onView(withId(R.id.button_select_category_select_picture_fragment)).perform(click())
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
        onView(withId(R.id.button_select_category_select_picture_fragment)).perform(click())
        onView(withText("cat1")).perform(click())
        intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(
            Intent().run {
                data = imageUri
                Instrumentation.ActivityResult(SelectPictureFragment.RC_OPEN_PICTURE, this)
            })
        onView(withId(R.id.button_choose_picture)).perform(click())
        onView(withId(R.id.button_save_select_picture_fragment)).perform(click())
        verify(navController).popBackStack()
    }

    private fun launchFragment() {
        val args = bundleOf("categories" to catSet.toTypedArray(), "datasetId" to datasetId)
        launchFragmentInHiltContainer<SelectPictureFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}
