package com.github.HumanLearning2021.HumanLearningApp

import android.os.Parcelable
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
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.presenter.DummyUIPresenter
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anything
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

            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(knife.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(spoon.name)!!)

            onData(anything())
                .inAdapterView(withId(R.id.display_dataset_imagesGridView))
                .atPosition(randomNb)
                .perform(click())

            intended(
                allOf(
                    hasComponent(DisplayImageActivity::class.java.name),
                    hasExtra("display_image_image", (datasetImagesList[randomNb]) as Parcelable)
                )
            )
        }
    }

}