package com.github.HumanLearning2021.HumanLearningApp

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
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
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class DisplayDatasetActivityTest {
    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayDatasetActivity::class.java)

    private val NUMBER_OF_CAT = 3
    private val datasetImagesList = ArrayList<CategorizedPicture>()
    private val staticDBManagement = DummyDatabaseManagement.staticDummyDatabaseManagement
    private var datasetId = "kitchen utensils"

    private val dummyPresenter = DummyUIPresenter(DummyDatabaseService())

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

        val randomNb = (0 until NUMBER_OF_CAT - 1).random()

        runBlocking {
            val categories = staticDBManagement.getCategories()
            for (cat in categories) {
                datasetImagesList.add(dummyPresenter.getPicture(cat.name)!!)
            }

            onData(anything())
                .inAdapterView(withId(R.id.display_dataset_imagesGridView))
                .atPosition(randomNb)
                .perform(click())

            intended(
                allOf(
                    hasComponent(DisplayImageSetActivity::class.java.name),
                    hasExtra(
                        "category_of_pictures",
                        categories.elementAt(randomNb)
                    ),
                    hasExtra("dataset_id", datasetId)
                )
            )
        }
    }

//    @Test
//    fun modifyingDatasetNameWorks() {
//        val newName = "new dataset name"
//        onView(withId(R.id.display_dataset_name)).perform((typeText("$newName\n")))
//        onView(withId(R.id.display_dataset_name)).check(matches(withText(containsString(newName))))
//    }

    @Test
    fun clickOnMenuModifyCategoriesWorks() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText("Modify categories")).perform(click())

        intended(
            allOf(
                hasComponent(DataCreationActivity::class.java.name),
                hasExtraWithKey("dataset_id"),
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

        pressBack()

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.add_new_picture)).perform(click())

        onView(withId(R.id.takePictureButton)).perform(click())
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("Fork")).perform(click())
        onView(withId(R.id.saveButton)).perform(click())

        onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
        val forkCat = DummyCategory("Fork", "Fork")
        runBlocking {
            assert(staticDBManagement.getAllPictures(forkCat).size == 2)
        }
    }

    @Test
    fun clickOnMenuButNotOnButtonClosesMenu() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(0, 100)
        onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
    }

}