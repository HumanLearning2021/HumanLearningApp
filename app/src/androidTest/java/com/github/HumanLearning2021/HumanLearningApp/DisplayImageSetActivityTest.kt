package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageSetActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayImageSetActivityTest {

    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayImageSetActivity::class.java, false, false)

    private var dsPictures = emptySet<CategorizedPicture>()
    private var categories = emptySet<Category>()
    private val dbName = "scratch"
    private val dbManagement = FirestoreDatabaseManagement(dbName)
    private lateinit var dataset : Dataset
    private lateinit var datasetId : String

    @Before
    fun setUp() {
        runBlocking {
            var found = false
            val datasets = dbManagement.getDatasets()
            for(ds in datasets){
                if(ds.categories.isNotEmpty() && !found){
                    dataset = ds
                    found = true
                }
            }
            datasetId = dataset.id as String
            categories = dataset.categories
            dsPictures = dbManagement.getAllPictures(categories.elementAt(0))
            val intent = Intent()
            intent.putExtra("category_of_pictures", (categories.elementAt(0)))
            intent.putExtra("dataset_id", datasetId)
            intent.putExtra("database_name", dbName)
            activityRuleIntent.launchActivity(intent)
            waitFor(1000)
        }
    }

    @Test
    fun imageSetGridAndNameAreDisplayed() {
        onView(withId(R.id.display_image_set_imagesGridView)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        onView(withId(R.id.display_image_set_name)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun imageIsDisplayedOnClick() {
        if(dsPictures.isNotEmpty()) {
            waitFor(1000)
            onView(withId(R.id.display_image_set_imagesGridView)).check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )

            onData(CoreMatchers.anything())
                .inAdapterView(withId(R.id.display_image_set_imagesGridView))
                .atPosition(0)
                .perform(click())

            Intents.intended(
                CoreMatchers.allOf(
                    IntentMatchers.hasComponent(DisplayImageActivity::class.java.name),
                    IntentMatchers.hasExtra(
                        "single_picture",
                        dsPictures.elementAt(0)
                    ),
                    IntentMatchers.hasExtra("dataset_id", datasetId),
                    IntentMatchers.hasExtra("database_name", dbName)
                )
            )
        }
    }

}