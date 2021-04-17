package com.github.HumanLearning2021.HumanLearningApp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
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
import com.github.HumanLearning2021.HumanLearningApp.hilt.DemoDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageSetActivity
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
import kotlin.collections.ArrayList

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DisplayImageActivityTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayImageActivity::class.java, false, false)

    @Inject
    @ScratchDatabase
    lateinit var dbManagement: DatabaseManagement

    private var dsPictures = emptySet<CategorizedPicture>()
    private lateinit var dataset: Dataset
    private lateinit var datasetId: String
    private var index = 0
    private lateinit var categories: Set<Category>
    private val categoryImagesList = ArrayList<CategorizedPicture>()

    @Before
    fun setUp() {
        hiltRule.inject()  // to get dbManagement set up
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
            datasetId = dataset.id as String
            categories = dataset.categories
            dsPictures = dbManagement.getAllPictures(categories.elementAt(index))
            val intent = Intent()
            if (dsPictures.isNotEmpty()) {
                intent.putExtra("single_picture", (dsPictures.elementAt(0)))
            }
            intent.putExtra("dataset_id", datasetId)
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
}
