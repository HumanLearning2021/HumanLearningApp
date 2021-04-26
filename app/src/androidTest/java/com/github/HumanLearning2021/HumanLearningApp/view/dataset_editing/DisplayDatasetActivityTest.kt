package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DisplayDatasetActivityTest {
    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayDatasetActivity::class.java, false, false)
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private var datasetPictures = emptySet<CategorizedPicture>()
    private var categories = emptySet<Category>()
    private lateinit var dataset: Dataset
    private lateinit var datasetId: String
    private var index = 0

    @Inject
    @ScratchDatabase
    lateinit var dbManagement: DatabaseManagement

    @Before
    fun setUp() {
        hiltRule.inject()  // ensures dbManagement is available
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
            categories = emptySet()
            datasetPictures = emptySet()
            datasetId = dataset.id as String
            val intent = Intent()
            intent.putExtra("dataset_id", datasetId)
            activityRuleIntent.launchActivity(intent)
        }
    }

    /**
     * Check that the Grid with all the images of the dataset are displayed.
     */
    @Test
    fun datasetGridAndNameAreDisplayed() {
        onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
        onView(withId(R.id.display_dataset_name)).check(matches(isDisplayed()))
    }

    /**
     * Check that when an image is clicked, DisplayImageActivity is launched with the good data in it.
     */
    @ExperimentalCoroutinesApi
    @Test
    fun whenClickOnCategoryImageDisplayImageSetActivity() {
        runBlocking {
            categories = dataset.categories
            for (cat in categories) {
                datasetPictures = datasetPictures.plus(dbManagement.getAllPictures(cat))
            }
            waitFor(1000)
            assumeTrue(datasetPictures.isNotEmpty())

            onData(anything())
                .inAdapterView(withId(R.id.display_dataset_imagesGridView))
                .atPosition(0)
                .perform(click())

            intended(
                allOf(
                    hasComponent(DisplayImageSetActivity::class.java.name),
                    hasExtra(
                        "category_of_pictures",
                        categories.elementAt(index)
                    ),
                    hasExtra("dataset_id", datasetId),
                )
            )
        }

    }

    @Test
    fun modifyingDatasetNameWorks() {
        val newName = "new dataset name"
        runBlocking {
            waitFor(1000)
            onView(withId(R.id.display_dataset_name)).perform(clearText(), typeText("$newName\n"))
            onView(withId(R.id.display_dataset_name)).check(matches(withText(containsString(newName))))
            dataset = dbManagement.getDatasetById(datasetId)!!
            assert(dataset.name == newName)
        }
    }

    @Test
    fun clickOnMenuModifyCategoriesWorks() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText("Modify categories")).perform(click())

        intended(
            allOf(
                hasComponent(CategoriesEditingActivity::class.java.name),
                hasExtra("dataset_id", datasetId),
            )
        )
    }

    @Test
    fun clickOnMenuAddNewPictureWorks() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.add_new_picture)).perform(click())

        intended(
            allOf(
                hasComponent(AddPictureActivity::class.java.name),
                hasExtraWithKey("categories"),
            )
        )

        Espresso.pressBack()

        categories = dataset.categories
        assumeTrue(categories.isNotEmpty())
        runBlocking {
            val numberOfPictures = dbManagement.getAllPictures(categories.elementAt(index)).size

            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            onView(withText(R.string.add_new_picture)).perform(click())

            onView(withId(R.id.takePictureButton)).perform(click())
            onView(withId(R.id.selectCategoryButton)).perform(click())
            onView(withText(categories.elementAt(index).name)).perform(click())
            onView(withId(R.id.saveButton)).perform(click())
            waitFor(3000)
            onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
            assert(dbManagement.getAllPictures(categories.elementAt(index)).size == numberOfPictures + 1)
        }


    }

    @Test
    fun clickOnMenuButNotOnButtonClosesMenu() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        UiDevice.getInstance(getInstrumentation()).click(0, 100)
        onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
    }

    @Test
    fun onBackPressedWorks() {
        Espresso.closeSoftKeyboard()
        val mDevice = UiDevice.getInstance(getInstrumentation())
        mDevice.pressBack()
        intended(
            allOf(
                hasComponent(DatasetsOverviewActivity::class.java.name),
            )
        )
    }

}
