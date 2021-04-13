package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
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
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.presenter.DummyUIPresenter
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageSetActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayDatasetActivityTest {
    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayDatasetActivity::class.java, false, false)

    private var datasetPictures = emptySet<CategorizedPicture>()
    private var categories = emptySet<Category>()
    private val dbName = "scratch"
    private val dbManagement = FirestoreDatabaseManagement(dbName)
    private lateinit var dataset : Dataset
    private lateinit var datasetId : String

    private val dummyPresenter = DummyUIPresenter(DummyDatabaseService())

    @Before
    fun setUp() {
        runBlocking {
            categories = emptySet()
            datasetPictures = emptySet()
            dataset = dbManagement.getDatasets().first()
            datasetId = dataset.id as String

            val intent = Intent()
            intent.putExtra("database_name", dbName)
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
            if (datasetPictures.isNotEmpty()) {

                onData(anything())
                    .inAdapterView(withId(R.id.display_dataset_imagesGridView))
                    .atPosition(0)
                    .perform(click())

                intended(
                    allOf(
                        hasComponent(DisplayImageSetActivity::class.java.name),
                        hasExtra(
                            "category_of_pictures",
                            categories.elementAt(0)
                        ),
                        hasExtra("dataset_id", datasetId),
                        hasExtra("database_name", dbName)
                    )
                )
            }
        }
    }

    @Test
    fun modifyingDatasetNameWorks() {
        val newName = "new dataset name"
        runBlocking {
            waitFor(1000)
            onView(withId(R.id.display_dataset_name)).perform(clearText(), typeText("$newName\n"))
            onView(withId(R.id.display_dataset_name)).check(matches(withText(containsString(newName))))
            dataset = dbManagement.getDatasets().first()
            assert(dataset.name == newName)
        }
    }

    @Test
    fun clickOnMenuModifyCategoriesWorks() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText("Modify categories")).perform(click())

        intended(
            allOf(
                hasComponent(DataCreationActivity::class.java.name),
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
        if(categories.isNotEmpty()) {
            runBlocking {
                val numberOfPictures = dbManagement.getAllPictures(categories.first()).size

                openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                onView(withText(R.string.add_new_picture)).perform(click())

                onView(withId(R.id.takePictureButton)).perform(click())
                onView(withId(R.id.selectCategoryButton)).perform(click())
                onView(withText(categories.first().name)).perform(click())
                onView(withId(R.id.saveButton)).perform(click())
                waitFor(3000)
                onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
                assert(dbManagement.getAllPictures(categories.first()).size == numberOfPictures + 1)
            }

        }
    }

    @Test
    fun clickOnMenuButNotOnButtonClosesMenu() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        UiDevice.getInstance(getInstrumentation()).click(0, 100)
        onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
    }

}