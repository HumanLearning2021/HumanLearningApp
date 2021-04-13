package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DataCreationActivityTest {

    private val dbName = "scratch"
    private val dbManagement = FirestoreDatabaseManagement(dbName)
    private lateinit var categories: Set<Category>
    private lateinit var dataset : Dataset
    private lateinit var datasetId : String

    @get:Rule
    var activityRuleIntent = IntentsTestRule(DataCreationActivity::class.java, false, false)

    @Before
    fun setUp() {
        runBlocking {
            dataset = dbManagement.getDatasets().first()
            categories = dataset.categories
            datasetId = dataset.id as String
            val categoriesArray = ArrayList<Category>(categories)
            val intent = Intent()
            intent.putExtra("dataset_id", datasetId)
            intent.putExtra("database_name", dbName)
            activityRuleIntent.launchActivity(intent)
            // By waiting before the test starts, it allows time for the app to startup to prevent the
            // following error to appear on cirrus:
            // `Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds.`
            // This solution is not ideal because it slows down the tests, and it might not work
            // every time. But there isn't a better solution that I (Niels Lachat) know of.
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
        val delayBeforeTestStart: Long = 100
        waitFor(delayBeforeTestStart)
        onView(withId(R.id.button_add)).perform(click())
        onView(withText("")).perform(typeText("new category"))
        if(categories.isEmpty()) {
            onView(withId(R.id.button_remove)).perform(click())
            onView(withId(R.id.parent_linear_layout)).check(
                ViewAssertions.matches(
                    hasChildCount(0)
                )
            )
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


}