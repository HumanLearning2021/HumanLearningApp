package com.github.HumanLearning2021.HumanLearningApp

import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import kotlinx.coroutines.runBlocking
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

object TestUtils {
    // inspired by : https://stackoverflow.com/a/35924943/7158887
    fun waitForAction(millis: Long): ViewAction =
        object : ViewAction {
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

            private val WARN_WAIT_TOO_LONG = 2000

            override fun perform(uiController: UiController?, view: View?) {
                if(millis > WARN_WAIT_TOO_LONG){
                    Log.w("TestUtils, wait", "PERFORMANCE : you use a long wait time > $WARN_WAIT_TOO_LONG" +
                            ", is this really necessary?")
                }
                uiController?.loopMainThreadForAtLeast(millis)
            }
        }

    fun waitFor(millis: Long) = onView(isRoot()).perform(waitForAction(millis))

    fun getFirstDataset(dbMgt : DatabaseManagement) = runBlocking {
        dbMgt.getDatasets().first()
    }
}