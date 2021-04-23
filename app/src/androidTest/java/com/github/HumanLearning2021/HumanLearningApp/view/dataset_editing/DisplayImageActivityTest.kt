package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.getFirstDataset
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningActivity
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningDatasetSelectionActivity
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningMode
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningSettingsActivity
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DisplayImageActivityTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbMgt: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private val datasetId: String = TestUtils.getFirstDataset(dbMgt).id as String
    @get:Rule(order = 1)
    val activityScenarioRule: ActivityScenarioRule<DisplayImageActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            DisplayImageActivity::class.java
        )
            .putExtra("dataset_id", datasetId)
    )


    private var dsPictures = emptySet<CategorizedPicture>()
    private var index = 0
    private val categories = runBlocking { getFirstDataset(dbMgt).categories }

    @Before
    fun setUp() {
        hiltRule.inject()  // to get dbManagement set up
        Intents.init()
//        runBlocking {
//            var found = false
//            val datasets = dbManagement.getDatasets()
//            for (ds in datasets) {
//                val dsCats = ds.categories
//                if (dsCats.isNotEmpty() && !found) {
//                    for (i in dsCats.indices) {
//                        val dsPictures = dbManagement.getAllPictures(dsCats.elementAt(i))
//                        if (dsPictures.isNotEmpty() && !found) {
//                            dataset = ds
//                            index = i
//                            found = true
//                        }
//                    }
//                }
//            }
//            if (!found) {
//                val cat = dbManagement.putCategory("${UUID.randomUUID()}")
//                dataset = dbManagement.putDataset("${UUID.randomUUID()}", setOf(cat))
//                val tmp = File.createTempFile("droid", ".png")
//                try {
//                    ApplicationProvider.getApplicationContext<Context>().resources.openRawResource(R.drawable.fork).use { img ->
//                        tmp.outputStream().use {
//                            img.copyTo(it)
//                        }
//                    }
//                    val uri = Uri.fromFile(tmp)
//                    dbManagement.putPicture(uri, cat)
//                } finally {
//                    tmp.delete()
//                }
//            }
//            datasetId = dataset.id as String
//            categories = dataset.categories
//            dsPictures = dbManagement.getAllPictures(categories.elementAt(index))
//            val intent = Intent()
//            if (dsPictures.isNotEmpty()) {
//                intent.putExtra("single_picture", (dsPictures.elementAt(0)))
//            }
//            intent.putExtra("dataset_id", datasetId)
//            activityRuleIntent.launchActivity(intent)
//            waitFor(1) // increase if needed
//        }
    }

    @After
    fun release(){
        Intents.release()
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
    fun deleteButtonWorks() { // TODO FIX ME
        var sameCatPictures: Set<CategorizedPicture>
        runBlocking {
            sameCatPictures = dbMgt.getAllPictures(categories.elementAt(index))
        }
        assumeTrue(sameCatPictures.isNotEmpty())
        onView(withId(R.id.display_image_delete_button)).perform(click())
        waitFor(1) // increase if needed
        if (sameCatPictures.size == 1) {
            Intents.intended(
                CoreMatchers.allOf( // TODO : this assertion fails
                    IntentMatchers.hasComponent(DisplayDatasetActivity::class.java.name),
                    IntentMatchers.hasExtra(
                        "dataset_id",
                        datasetId
                    ),
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
                )
            )
        }

    }

    @Test
    fun setAsRepresentativePictureButtonWorks() {
        onView(withId(R.id.display_image_set_representative_picture)).perform(click())
    }
}
