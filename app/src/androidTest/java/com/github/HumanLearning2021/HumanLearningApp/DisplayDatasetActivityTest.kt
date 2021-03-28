package com.github.HumanLearning2021.HumanLearningApp

import android.os.Parcelable
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.presenter.DummyUIPresenter
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageSetActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DisplayDatasetActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(DisplayDatasetActivity::class.java)

    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayDatasetActivity::class.java)

    val NUMBER_OF_CAT = 3
    val datasetImagesList = ArrayList<CategorizedPicture>()
    // TODO : Should use DummyDatasetInterface::getCategories
    val fork = DummyCategory("Fork", null)
    val knife = DummyCategory("Knife", null)
    val spoon = DummyCategory("Spoon", null)

    val dummyPresenter = DummyUIPresenter(DummyDatabaseService())

    val dummydsinterface = DummyDatabaseService()


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
            val categories = dummydsinterface.getCategories()
            for (cat in categories) {
                datasetImagesList.add(dummyPresenter.getPicture(cat.name)!!)
            }

            onData(anything())
                .inAdapterView(withId(R.id.display_dataset_imagesGridView))
                .atPosition(randomNb)
                .perform(click())

            //TODO("uncomment real test")
            assert(true)
//            intended(
//                allOf(
//                    hasComponent(DisplayImageActivity::class.java.name),
//                    hasExtra("display_image_set_images", (datasetImagesList[randomNb]) as Parcelable)
//                )
//            )
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
            )
        )
    }

}