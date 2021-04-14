package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageSetActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayImageActivityTest {
    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayImageActivity::class.java, false, false)

    private var dsPictures = emptySet<CategorizedPicture>()
    private var categories = emptySet<Category>()
    private val dbName = "scratch"
    private val dbManagement = FirestoreDatabaseManagement(dbName)
    private lateinit var dataset: Dataset
    private lateinit var datasetId: String
    private var index = 0

    @Before
    fun setUp() {
        runBlocking {
            var found = false
            val datasets = dbManagement.getDatasets()
            for (ds in datasets) {
                val dsCats = ds.categories
                if (dsCats.isNotEmpty() && !found) {
                    for (i in dsCats.indices) {
                        val dsPictures = dbManagement.getAllPictures(dsCats.elementAt(i))
                        if (dsPictures.isNotEmpty() && !found) {
                            dataset = ds
                            index = i
                            found = true
                        }
                    }
                }
            }
            datasetId = dataset.id as String
            categories = dataset.categories
            dsPictures = dbManagement.getAllPictures(categories.elementAt(index))
            val intent = Intent()
            if (dsPictures.isNotEmpty()) {
                intent.putExtra("single_picture", (dsPictures.elementAt(0)))
            }
            intent.putExtra("dataset_id", datasetId)
            intent.putExtra("database_name", dbName)
            activityRuleIntent.launchActivity(intent)
            waitFor(1000)
        }
    }

    @Test
    fun pictureAndCategoryAreDisplayed() {
        assumeTrue(dsPictures.isNotEmpty())
        onView(withId(R.id.display_image_viewImage))
            .check(matches(isDisplayed()))
        onView(withId(R.id.display_image_viewCategory))
            .check(matches(isDisplayed()))
        onView(withId(R.id.display_image_delete_button)).check(matches(isDisplayed()))

    }

    @Test
    fun deleteButtonWorks() {
        var sameCatPictures: Set<CategorizedPicture>
        runBlocking {
            sameCatPictures = dbManagement.getAllPictures(categories.elementAt(index))
        }
        assumeTrue(sameCatPictures.isNotEmpty())
        onView(withId(R.id.display_image_delete_button)).perform(click())
        waitFor(3000)
        if (sameCatPictures.size == 1) {
            Intents.intended(
                CoreMatchers.allOf(
                    IntentMatchers.hasComponent(DisplayDatasetActivity::class.java.name),
                    IntentMatchers.hasExtra(
                        "dataset_id",
                        datasetId
                    ),
                    IntentMatchers.hasExtra("database_name", dbName)
                )
            )
        } else {
            Intents.intended(
                CoreMatchers.allOf(
                    IntentMatchers.hasComponent(DisplayImageSetActivity::class.java.name),
                    IntentMatchers.hasExtra(
                        "category_of_pictures",
                        categories.elementAt(index)
                    ),
                    IntentMatchers.hasExtra(
                        "dataset_id",
                        datasetId
                    ),
                    IntentMatchers.hasExtra(
                        "database_name",
                        dbName
                    )
                )
            )
        }

    }
}