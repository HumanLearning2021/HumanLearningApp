package com.github.HumanLearning2021.HumanLearningApp

import android.view.View
import android.widget.Button
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matcher
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Test





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


    @get:Rule
    var testRule = ActivityTestRule(DataCreationActivity::class.java)

    @Before
    fun setUp() {
        // By waiting before the test starts, it allows time for the app to startup to prevent the
        // following error to appear on cirrus:
        // `Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds.`
        // This solution is not ideal because it slows down the tests, and it might not work
        // every time. But there isn't a better solution that I (Niels Lachat) know of.
        val delayBeforeTestStart: Long = 3000
        onView(isRoot()).perform(waitFor(delayBeforeTestStart))
    }



    @Test
    fun rowViewIsDsiplayedWhenAddButtonIsClicked() {
        onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())
        onView(ViewMatchers.withHint("Enter Category")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


    }

    @Test
    fun rowButtonViewIsDsiplayedWhenAddButtonIsClicked(){
        onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.button_add)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


    }

    @Test
    fun rowViewIsAddedWhenAddButtonIsClicked(){
        onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.parent_linear_layout)).check(ViewAssertions.matches(
                ViewMatchers.hasChildCount(1)))


    }
    @Test
    fun rowViewIsRemovedWhenRemoveButtonIsClicked(){
        onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())
        val delayBeforeTestStart: Long = 3000
        onView(isRoot()).perform(waitFor(delayBeforeTestStart))
        onView(ViewMatchers.withId(R.id.button_remove)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.parent_linear_layout)).check(ViewAssertions.matches(
            ViewMatchers.hasChildCount(0)))

    }

}