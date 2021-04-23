package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.*

@UninstallModules(DatabaseManagementModule::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class CategoriesEditingActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbMgt: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private lateinit var categories: Set<Category>
    private var nbCategories = 0
    private lateinit var dataset: Dataset
    private lateinit var datasetId: String

    @get:Rule
    var activityRuleIntent = IntentsTestRule(CategoriesEditingActivity::class.java, false, false)

    @Before
    fun setUp() {
        hiltRule.inject()  // to get staticDBManagement set up
        runBlocking {
            var found = false
            val datasets = dbMgt.getDatasets()
            for (ds in datasets) {
                val dsCats = ds.categories
                if (dsCats.size == 1 && !found) {
                    dataset = ds
                    found = true
                }
            }
            if (!found) {
                val cat = dbMgt.putCategory("${UUID.randomUUID()}")
                dataset = dbMgt.putDataset("${UUID.randomUUID()}", setOf(cat))
                val tmp = File.createTempFile("droid", ".png")
                try {
                    ApplicationProvider.getApplicationContext<Context>().resources.openRawResource(R.drawable.fork).use { img ->
                        tmp.outputStream().use {
                            img.copyTo(it)
                        }
                    }

                    val uri = Uri.fromFile(tmp)
                    dbMgt.putPicture(uri, cat)
                } finally {
                    tmp.delete()
                }
            }
            categories = dataset.categories
            nbCategories = categories.size
            datasetId = dataset.id as String
            val intent = Intent()
            intent.putExtra("dataset_id", datasetId)
            activityRuleIntent.launchActivity(intent)

            val delayBeforeTestStart: Long = 1 // increase if needed
            waitFor(delayBeforeTestStart)
        }
    }

    @Test
    fun rowViewIsDisplayedWhenAddButtonIsClicked() {
        onView(withId(R.id.button_add)).perform(click())
        onView(withText("")).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun rowButtonViewIsDisplayedWhenAddButtonIsClicked() {
        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.button_add)).check(ViewAssertions.matches(isDisplayed()))


    }

    @Test
    fun rowViewIsAddedWhenAddButtonIsClicked() {
        onView(withId(R.id.button_add)).perform(click())
        waitFor(1) // increase if needed
        onView(withId(R.id.parent_linear_layout)).check(
            ViewAssertions.matches(
                hasChildCount(categories.size + 1)
            )
        )
    }

    @Test
    fun rowViewIsRemovedWhenRemoveButtonIsClicked() {
        runBlocking {
            waitFor(1) // increase if needed
            assumeTrue(categories.size == 1)
            onView(withId(R.id.button_remove)).perform(click())
            waitFor(1)
            onView(withId(R.id.parent_linear_layout)).check(
                ViewAssertions.matches(
                    hasChildCount(0)
                )
            )
            dataset = dbMgt.getDatasetById(datasetId)!!
            assert(nbCategories - 1 == dataset.categories.size)
        }

    }

    @Test
    fun saveButtonGoesToDisplayDatasetActivity() {
        onView(withId(R.id.button_submit_list)).perform(click())
        waitFor(1) // increase if needed
        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasComponent(DisplayDatasetActivity::class.java.name),
                IntentMatchers.hasExtra("dataset_id", datasetId),
            )
        )
    }

    //TODO restore
//    @Test
    fun addNewCategoryToDatasetWorks() {
            onView(withId(R.id.button_add)).perform(click())
            onView(withText("")).perform(typeText("new beautiful category"))
            onView(withId(R.id.button_submit_list)).perform(click())
            waitFor(1) // increase id needed
        runBlocking {
            dataset = dbMgt.getDatasetById(datasetId)!!
        }
            assert(nbCategories + 1 == dataset.categories.size)
    }
}
