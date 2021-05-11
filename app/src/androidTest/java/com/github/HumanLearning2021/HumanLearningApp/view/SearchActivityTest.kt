package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.KeyEvent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SearchActivityTest {


    @get:Rule
    val hiltRule = HiltAndroidRule(this)


    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<SearchActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            SearchActivity::class.java
        )
    )

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    @RoomDatabase
    lateinit var room: RoomOfflineDatabase

    @BindValue
    @Demo2Database
    lateinit var dbMgt: DatabaseManagement


    @Before
    fun setUp() {
        Intents.init()
        dbMgt = DefaultDatabaseManagement(DummyDatabaseService(), "dummy", context, room)
        val delayBeforeTestStart: Long = 50
        TestUtils.waitFor(delayBeforeTestStart)
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }

    @Test
    fun datasetNamesAreDisplayed() {
        assertDisplayed(R.id.listView)
        assertDisplayed(R.id.searchView)
    }

    @Test
    fun searchByKeyWordYieldsCorrectResult() {
        onView(
            withId(
                Resources.getSystem().getIdentifier(
                    "search_src_text",
                    "id", "android"
                )
            )
        ).perform(clearText(), typeText("Kitchen"))
            .perform(pressKey(KeyEvent.KEYCODE_ENTER))

        onView(withId(R.id.listView)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(1)
            )
        )


    }

    @Test
    fun searchNotFoundYieldsNoResult() {
        onView(
            withId(
                Resources.getSystem().getIdentifier(
                    "search_src_text",
                    "id", "android"
                )
            )
        ).perform(clearText(), typeText("toto"))
            .perform(pressKey(KeyEvent.KEYCODE_ENTER))

        onView(withId(R.id.listView)).check(
            ViewAssertions.matches(
                ViewMatchers.hasChildCount(0)
            )
        )


    }


}