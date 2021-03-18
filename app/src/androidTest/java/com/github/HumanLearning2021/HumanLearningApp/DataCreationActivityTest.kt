package com.github.HumanLearning2021.HumanLearningApp

import android.widget.Button
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Test





@RunWith(AndroidJUnit4::class)
class DataCreationActivityTest {

    @get:Rule
    var testRule = ActivityTestRule(DataCreationActivity::class.java)


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
        onView(ViewMatchers.withId(R.id.button_remove)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.parent_linear_layout)).check(ViewAssertions.matches(
            ViewMatchers.hasChildCount(0)))

    }

}