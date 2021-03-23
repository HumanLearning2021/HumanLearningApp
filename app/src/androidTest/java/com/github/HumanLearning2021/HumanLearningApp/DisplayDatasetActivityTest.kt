package com.github.HumanLearning2021.HumanLearningApp

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatasetInterface
import com.github.HumanLearning2021.HumanLearningApp.presenter.DummyUIPresenter
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageSetActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anything
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
    val dummydsinterface = DummyDatasetInterface()
    val dummyPresenter = DummyUIPresenter(DummyDatasetInterface())


    /**
     * Check that the Grid with all the images of the dataset are displayed.
     */
    @Test
    fun DatasetGridIsDisplayed() {
        onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
    }

    /**
     * Check that when an image is clicked, DisplayImageActivity is launched with the good data in it.
     */
    @ExperimentalCoroutinesApi
    @Test
    fun WhenClickOnImageDisplayImageActivityAndCorrectImage() {

        val randomNb = (0 until NUMBER_OF_CAT).random()

        runBlocking {
            val categories = dummydsinterface.getCategories()
            for (cat in categories){
                datasetImagesList.add(dummyPresenter.getPicture(cat.name)!!)
            }

            onData(anything())
                .inAdapterView(withId(R.id.display_dataset_imagesGridView))
                .atPosition(randomNb)
                .perform(click())

            intended(
                allOf(
                    hasComponent(DisplayImageSetActivity::class.java.name),
                    hasExtra("display_image_set_images", (datasetImagesList[randomNb]) as Serializable)
                )
            )
        }
    }

}