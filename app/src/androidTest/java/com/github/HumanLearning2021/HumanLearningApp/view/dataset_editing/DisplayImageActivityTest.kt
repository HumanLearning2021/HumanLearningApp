package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.ContentResolver
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
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.getFirstDataset
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
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DisplayImageActivityTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbMgt: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private val dataset = getFirstDataset(dbMgt)

    private lateinit var categoryWith1Picture : Category
    private lateinit var categoryWith2Pictures : Category

    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayImageActivity::class.java, false, false)

//    @get:Rule(order = 1)
//    val activityScenarioRule: ActivityScenarioRule<DisplayImageActivity> = ActivityScenarioRule(
//        Intent(
//            ApplicationProvider.getApplicationContext(),
//            DisplayImageActivity::class.java
//        )
//            .putExtra("dataset_id", datasetId)
//            .putExtra("single_picture", (picturesInCategory.elementAt(0)))
//    )

    @Before
    fun setUp() {
        hiltRule.inject()  // to get dbManagement set up
        Intents.init()

        categoryWith1Picture = newCategoryWithNPictures(1)
        categoryWith2Pictures = newCategoryWithNPictures(2)

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
        activityRuleIntent.launchActivity(Intent()
            .putExtra("dataset_id", dataset.id as String)
        )
//
//        assumeTrue(picturesInCategory.isNotEmpty())
        onView(withId(R.id.display_image_viewImage))
            .check(matches(isDisplayed()))
        onView(withId(R.id.display_image_viewCategory))
            .check(matches(isDisplayed()))
        onView(withId(R.id.display_image_delete_button)).check(matches(isDisplayed()))
    }

    private fun getForkUri(): Uri {
        // could maybe be made simpler
        val res = ApplicationProvider.getApplicationContext<Context>().resources
        val rId = R.drawable.fork
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(rId) +
                "/" + res.getResourceTypeName(rId) +
                "/" + res.getResourceEntryName(rId)
        )
    }

    private fun newCategoryWithNPictures(N : Int): Category {
        require(N > 0)

        return runBlocking {
            val newCategory = dbMgt.putCategory("${UUID.randomUUID()}")

            // TODO : java.lang.IllegalArgumentException: The underlying database does not contain the dataset kitchen utensils
            dbMgt.addCategoryToDataset(dataset, newCategory)

            val forkUri = getForkUri()
            for(i in 1..N){
                dbMgt.putPicture(forkUri, newCategory)
            }
            newCategory
        }
    }

    @Test
    fun deleteLastImageOfCategory() {
        onView(withId(R.id.display_image_delete_button)).perform(click())
        waitFor(1) // increase if needed
        val updatedPicturesInCategory = runBlocking {
            dbMgt.getAllPictures(categoryWith1Picture)
        }
        assumeTrue(updatedPicturesInCategory.isEmpty())
        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasComponent(DisplayDatasetActivity::class.java.name),
                IntentMatchers.hasExtra(
                    "dataset_id",
                    dataset.id as String
                ),
            )
        )
    }

    @Test
    fun deleteNotLastImageInCategory() {
        onView(withId(R.id.display_image_delete_button)).perform(click())
        waitFor(1) // increase if needed
        val updatedPicturesInCategory = runBlocking {
            dbMgt.getAllPictures(categoryWith1Picture)
        }
        assumeTrue(updatedPicturesInCategory.isEmpty())
        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasComponent(DisplayImageSetActivity::class.java.name),
                IntentMatchers.hasExtra(
                    "category_of_pictures",
                    categoryWith2Pictures
                ),
                IntentMatchers.hasExtra(
                    "dataset_id",
                    dataset.id as String
                ),
            )
        )
    }

    @Test
    fun setAsRepresentativePictureButtonWorks() {
        onView(withId(R.id.display_image_set_representative_picture)).perform(click())
        // TODO test that the functionality is correctly implemented
    }
}
