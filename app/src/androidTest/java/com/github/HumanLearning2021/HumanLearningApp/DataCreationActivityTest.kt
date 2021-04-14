package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DataCreationActivityTest {

    private val dbName = "scratch"
    private val dbManagement = FirestoreDatabaseManagement(dbName)
    private lateinit var categories: Set<Category>
    private var nbCategories = 0
    private lateinit var dataset: Dataset
    private lateinit var datasetId: String

    @get:Rule
    var activityRuleIntent = IntentsTestRule(DataCreationActivity::class.java, false, false)

    @Before
    fun setUp() {
        runBlocking {
            var found = false
            val datasets = dbManagement.getDatasets()
            for (ds in datasets) {
                val dsCats = ds.categories
                if (dsCats.size == 1 && !found) {
                    dataset = ds
                    found = true
                }
            }
            categories = dataset.categories
            nbCategories = categories.size
            datasetId = dataset.id as String
            val intent = Intent()
            intent.putExtra("dataset_id", datasetId)
            intent.putExtra("database_name", dbName)
            activityRuleIntent.launchActivity(intent)

            val delayBeforeTestStart: Long = 3000
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
        onView(withId(R.id.parent_linear_layout)).check(
            ViewAssertions.matches(
                hasChildCount(categories.size + 1)
            )
        )
    }

    @Test
    fun rowViewIsRemovedWhenRemoveButtonIsClicked() {
        runBlocking {
            val delayBeforeTestStart: Long = 100
            waitFor(delayBeforeTestStart)
            assumeTrue(categories.size == 1)
            onView(withId(R.id.button_remove)).perform(click())
            waitFor(1000)
            onView(withId(R.id.parent_linear_layout)).check(
                ViewAssertions.matches(
                    hasChildCount(0)
                )
            )
            dataset = dbManagement.getDatasetById(datasetId)!!
            assert(nbCategories - 1 == dataset.categories.size)
        }

    }

    @Test
    fun saveButtonGoesToDisplayDatasetActivity() {
        onView(withId(R.id.button_submit_list)).perform(click())
        waitFor(1000)
        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasComponent(DisplayDatasetActivity::class.java.name),
                IntentMatchers.hasExtra("dataset_id", datasetId),
                IntentMatchers.hasExtra("database_name", dbName)
            )
        )
    }

    @Test
    fun addNewCategoryToDatasetWorks() {
        runBlocking {
            onView(withId(R.id.button_add)).perform(click())
            onView(withText("")).perform(typeText("new beautiful category"))
            onView(withId(R.id.button_submit_list)).perform(click())
            waitFor(500)
            dataset = dbManagement.getDatasetById(datasetId)!!
            assert(nbCategories + 1 == dataset.categories.size)
        }
    }
}