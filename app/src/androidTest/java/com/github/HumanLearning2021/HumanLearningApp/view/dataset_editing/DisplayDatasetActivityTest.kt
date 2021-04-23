package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DisplayDatasetActivityTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbMgt: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private val datasetId: String = getFirstDataset(dbMgt).id as String
    @get:Rule(order = 1)
    val activityScenarioRule: ActivityScenarioRule<DisplayDatasetActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            DisplayDatasetActivity::class.java
        ).putExtra("dataset_id", datasetId)
    )

    private var datasetPictures = emptySet<CategorizedPicture>()
    private var categories = emptySet<Category>()
    private lateinit var dataset: Dataset
    private var index = 0


    @Before
    fun setUp() {
        hiltRule.inject()  // ensures dbManagement is available
        dataset = getFirstDataset(dbMgt)
        Intents.init()
    }

    @After
    fun release(){
        Intents.release()
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
                datasetPictures = datasetPictures.plus(dbMgt.getAllPictures(cat))
            }
            waitFor(1) // increase if needed
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
            waitFor(1) // increase if needed
            onView(withId(R.id.display_dataset_name)).perform(clearText(), typeText("$newName\n"))
            onView(withId(R.id.display_dataset_name)).check(matches(withText(containsString(newName))))

            // need to get again because dataset is immutable and editing the name creates a new
            // Dataset object in the database
            dataset = getFirstDataset(dbMgt)
            assert(dataset.name == newName) {
                "dataset name \"${dataset.name}\" different" +
                        " from \"$newName\""
            }
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
            val numberOfPictures = dbMgt.getAllPictures(categories.elementAt(index)).size

            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            onView(withText(R.string.add_new_picture)).perform(click())

            onView(withId(R.id.takePictureButton)).perform(click())
            onView(withId(R.id.selectCategoryButton)).perform(click())
            onView(withText(categories.elementAt(index).name)).perform(click())
            onView(withId(R.id.saveButton)).perform(click())
            waitFor(1) // increase if needed
            onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
            assert(dbMgt.getAllPictures(categories.elementAt(index)).size == numberOfPictures + 1)
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
