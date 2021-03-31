package com.github.HumanLearning2021.HumanLearningApp

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.presenter.DummyUIPresenter
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageSetActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.Serializable


@RunWith(AndroidJUnit4::class)
class DisplayDatasetActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(DisplayDatasetActivity::class.java)

    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayDatasetActivity::class.java)

    val NUMBER_OF_CAT = 3
    val datasetImagesList = ArrayList<CategorizedPicture>()
    val databaseService = DummyDatabaseService()
    val dummyPresenter = DummyUIPresenter(DummyDatabaseService())

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

        val randomNb = (0 until NUMBER_OF_CAT).random()

        runBlocking {
            val categories = databaseService.getCategories()
            for (cat in categories) {
                datasetImagesList.add(dummyPresenter.getPicture(cat.name)!!)
            }

            onData(anything())
                .inAdapterView(withId(R.id.display_dataset_imagesGridView))
                .atPosition(randomNb)
                .perform(click())
            val allPictures = databaseService.pictures
            val catPictures: MutableSet<CategorizedPicture> = mutableSetOf()
            for (p in allPictures) {
                if (p.category == categories.elementAt(randomNb)) {
                    catPictures.add(p)
                }
            }

            intended(
                allOf(
                    hasComponent(DisplayImageSetActivity::class.java.name),
                    hasExtra(
                        "display_image_set_images",
                        (catPictures) as Serializable
                    )
                )
            )


        }
    }

    @Test
    fun modifyingDatasetNameWorks() {
        onView(withId(R.id.display_dataset_name)).perform((typeText("Dataset Name\n")))
        onView(withId(R.id.display_dataset_name)).check(matches(withText(containsString("Dataset Name"))))
    }

    @Test
    fun clickOnMenuModifyCategoriesWorks() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText("Modify categories")).perform(click())

        intended(
            allOf(
                hasComponent(DataCreationActivity::class.java.name),
                hasExtraWithKey("dataset_categories"),
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
    }

    @Test
    fun clickOnMenuButNotOnButtonClosesMenu() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(0, 100)
        onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
    }

}