package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DataCreationActivityTest {

    // inspired by : https://stackoverflow.com/a/35924943/7158887
    private fun waitFor(millis: Long): ViewAction =
        object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds"
            }

            override fun perform(uiController: UiController?, view: View?) {
                uiController?.loopMainThreadForAtLeast(millis)
            }
        }

    private val staticDBManagement = DummyDatabaseManagement.staticDummyDatabaseManagement
    private lateinit var categories: Set<Category>

    @get:Rule
    var activityRuleIntent = IntentsTestRule(DataCreationActivity::class.java, false, false)

    @Before
    fun setUp() {
        runBlocking {
            categories = staticDBManagement.getCategories()
            categories = categories.minus(categories.elementAt(0))
            categories = categories.minus(categories.elementAt(0))
        }
        val categoriesArray = ArrayList<Category>(categories)
        val intent = Intent()
        intent.putParcelableArrayListExtra("dataset_categories", categoriesArray)
        activityRuleIntent.launchActivity(intent)
        // By waiting before the test starts, it allows time for the app to startup to prevent the
        // following error to appear on cirrus:
        // `Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds.`
        // This solution is not ideal because it slows down the tests, and it might not work
        // every time. But there isn't a better solution that I (Niels Lachat) know of.
        val delayBeforeTestStart: Long = 3000
        onView(isRoot()).perform(waitFor(delayBeforeTestStart))
    }

    @Test
    fun rowViewIsDisplayedWhenAddButtonIsClicked() {
        onView(withId(R.id.button_add)).perform(ViewActions.click())
        onView(withText("Spoon")).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun rowButtonViewIsDisplayedWhenAddButtonIsClicked() {
        onView(withId(R.id.button_add)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.button_add)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


    }

    @Test
    fun rowViewIsAddedWhenAddButtonIsClicked() {
        onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.parent_linear_layout)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(categories.size + 1)
            )
        )
    }

    @Test
    fun rowViewIsRemovedWhenRemoveButtonIsClicked() {
        val delayBeforeTestStart: Long = 100
        onView(isRoot()).perform(waitFor(delayBeforeTestStart))
        onView(withId(R.id.button_remove)).perform(click())
        onView(withId(R.id.parent_linear_layout)).check(
            ViewAssertions.matches(
                hasChildCount(0)
            )
        )
    }

    @Test
    fun SaveButtonGoesToDisplayDatasetActivity() {
        onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())
        onView(withId(R.id.button_submit_list)).perform(click())

        Intents.intended(
            CoreMatchers.allOf(
                IntentMatchers.hasComponent(DisplayDatasetActivity::class.java.name),
            )
        )
    }


}