package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing.DatasetsOverviewActivity
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


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

    @BindValue
    @Demo2Database
    val dbMgt: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())




    @Before
    fun setUp() {
        Intents.init()
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



}