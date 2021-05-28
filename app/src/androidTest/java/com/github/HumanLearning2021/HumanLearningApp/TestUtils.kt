package com.github.HumanLearning2021.HumanLearningApp

import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import kotlinx.coroutines.runBlocking
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.both
import org.hamcrest.TypeSafeMatcher

object TestUtils {
    val WAIT_FOR_WARNING_THRESHOLD = 100
    val WAIT_FOR_ERROR_THRESHOLD = 1001

    /**
     * ViewAction allowing to wait for a certain number of milliseconds
     * @param millis Number of milliseconds to wait for
     * @see TestUtils.WAIT_FOR_WARN_THRESHOLD Upper bound for the millis parameter. If not respected
     * -> warning.
     * @see TestUtils.WAIT_FOR_ERROR_THRESHOLD Upper bound for the millis parameter. If not respected
     * -> the program crashes.
     * These bounds are in place to avoid abusing the wait and making tests
     * uselessly slow.
     *
     * inspired by : https://stackoverflow.com/a/35924943/7158887
     */
    fun waitForAction(millis: Long): ViewAction {
        require(millis < WAIT_FOR_ERROR_THRESHOLD) {
            "Waiting for such a long time ($millis ms) is probably useless"
        }
        if (millis > WAIT_FOR_WARNING_THRESHOLD) {
            Log.w(
                "TestUtils, wait",
                "PERFORMANCE : you use a long wait time > $WAIT_FOR_WARNING_THRESHOLD" +
                        ", is this really necessary?"
            )
        }
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return object : BaseMatcher<View>() {
                    override fun describeTo(description: Description?) {
                        description?.appendText("matches anything")
                    }

                    override fun matches(item: Any?): Boolean {
                        return true
                    }

                    override fun describeMismatch(item: Any?, mismatchDescription: Description?) {
                        mismatchDescription?.appendText("mismatched (should never happen)")
                    }

                }
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds"
            }

            override fun perform(uiController: UiController?, view: View?) {
                uiController?.loopMainThreadForAtLeast(millis)
            }
        }
    }

    /**
     * Waits by looping on the main thread for the given amount of time
     * @param millis Number of milliseconds to wait for
     * @see TestUtils.waitForAction for restrictions on maximum accepted value of parameter
     */
    fun waitFor(millis: Long) = onView(isRoot()).perform(waitForAction(millis))

    fun getFirstDataset(dbMgt: DatabaseManagement) = runBlocking {
        dbMgt.getDatasets().first()
    }

    // inspired by https://stackoverflow.com/a/39756832/7158887
    fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            var currentIndex = 0
            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View?): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }

    /**
     * Returns a dataset with N categories in the given database
     * @param N number of categories of the dataset
     * @param dbMgt DatabaseManagement instance
     * @return first dataset with N categories
     * @throws IllegalArgumentException if no dataset with N categories found
     */
    fun getDatasetWithNCategories(N: Int, dbMgt: DatabaseManagement): Dataset {
        require(N > 0)
        val maybeDataset = runBlocking {
            dbMgt.getDatasets().find { it.categories.size == N }
        }
        require(maybeDataset != null)
        { "There has to be a dataset with $N categories in $dbMgt" }
        return maybeDataset
    }

    // per https://stackoverflow.com/a/60032604
    fun setTextInSearchView(value: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> =
                both(
                    isDisplayed()
                ).and(
                    isAssignableFrom(SearchView::class.java)
                )

            override fun perform(uiController: UiController, view: View) {
                view as SearchView
                view.setQuery(value, false)
            }

            override fun getDescription(): String = "replace text($value)"
        }
    }
}