package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageSetActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayImageActivityTest {
    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayImageActivity::class.java, false, false)

    private lateinit var categories: Set<Category>
    private val categoryImagesList = ArrayList<CategorizedPicture>()
    private val staticDBManagement = DummyDatabaseManagement.staticDummyDatabaseManagement

    @Before
    fun setUp() {
        runBlocking {
            categories = staticDBManagement.getCategories()
            categoryImagesList.add(staticDBManagement.getPicture(categories.elementAt(0))!!)
        }
        val intent = Intent()
        intent.putExtra("single_picture", (categoryImagesList[0]))
        intent.putExtra("dataset_id", "kitchen utensils")
        activityRuleIntent.launchActivity(intent)
    }

    @Test
    fun pictureAndCategoryAreDisplayed() {
        onView(withId(R.id.display_image_viewImage))
            .check(matches(isDisplayed()))
        onView(withId(R.id.display_image_viewCategory))
            .check(matches(isDisplayed()))
        onView(withId(R.id.display_image_delete_button)).check(matches(isDisplayed()))
    }

    @Test
    fun deleteButtonWorks1() {
        var sameCatPictures: Set<CategorizedPicture>
        runBlocking {
            sameCatPictures = staticDBManagement.getAllPictures(categories.elementAt(0))
        }
        onView(withId(R.id.display_image_delete_button)).perform(click())
        if (sameCatPictures.size == 1) {
            Intents.intended(
                CoreMatchers.allOf(
                    IntentMatchers.hasComponent(DisplayDatasetActivity::class.java.name),
                    IntentMatchers.hasExtra(
                        "dataset_id",
                        "kitchen utensils"
                    )
                )
            )
        } else {
            Intents.intended(
                CoreMatchers.allOf(
                    IntentMatchers.hasComponent(DisplayImageSetActivity::class.java.name),
                    IntentMatchers.hasExtra(
                        "category_of_pictures",
                        categories.elementAt(0)
                    )
                )
            )
        }
    }

    @Test
    fun deleteButtonWorks2() {
        var sameCatPictures: Set<CategorizedPicture>
        runBlocking {
            sameCatPictures = staticDBManagement.getAllPictures(categories.elementAt(0))
        }
        onView(withId(R.id.display_image_delete_button)).perform(click())
        if (sameCatPictures.size == 1) {
            Intents.intended(
                CoreMatchers.allOf(
                    IntentMatchers.hasComponent(DisplayDatasetActivity::class.java.name),
                )
            )
        } else {
            Intents.intended(
                CoreMatchers.allOf(
                    IntentMatchers.hasComponent(DisplayImageSetActivity::class.java.name),
                    IntentMatchers.hasExtra(
                        "category_of_pictures",
                        categories.elementAt(0)
                    )
                )
            )
        }
    }

}