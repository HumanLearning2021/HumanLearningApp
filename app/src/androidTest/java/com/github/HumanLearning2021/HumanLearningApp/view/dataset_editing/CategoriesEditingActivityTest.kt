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
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.*
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class CategoriesEditingActivityTest {
    @Inject
    @ScratchDatabase
    lateinit var dbManagement: DatabaseManagement

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

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
            val datasets = dbManagement.getDatasets()
            for (ds in datasets) {
                val dsCats = ds.categories
                if (dsCats.size == 1 && !found) {
                    dataset = ds
                    found = true
                }
            }
            if (!found) {
                val cat = dbManagement.putCategory("${UUID.randomUUID()}")
                dataset = dbManagement.putDataset("${UUID.randomUUID()}", setOf(cat))
                val tmp = File.createTempFile("droid", ".png")
                try {
                    ApplicationProvider.getApplicationContext<Context>().resources.openRawResource(R.drawable.fork).use { img ->
                        tmp.outputStream().use {
                            img.copyTo(it)
                        }
                    }

                    val uri = Uri.fromFile(tmp)
                    dbManagement.putPicture(uri, cat)
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
            val delayBeforeTestStart: Long = 1000
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
            )
        )
    }

    @Test
    fun addNewCategoryToDatasetWorks() {
        runBlocking {
            onView(withId(R.id.button_add)).perform(click())
            onView(withText("")).perform(typeText("new beautiful category"))
            onView(withId(R.id.button_submit_list)).perform(click())
            waitFor(5000)
            dataset = dbManagement.getDatasetById(datasetId)!!
            assert(nbCategories + 1 == dataset.categories.size)
        }
    }
}
