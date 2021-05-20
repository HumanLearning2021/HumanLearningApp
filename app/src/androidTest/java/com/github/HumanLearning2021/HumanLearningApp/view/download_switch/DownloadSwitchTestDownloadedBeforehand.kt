package com.github.HumanLearning2021.HumanLearningApp.view.download_switch

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.GlobalDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.hilt.RoomDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.room.DatabaseDao
import com.github.HumanLearning2021.HumanLearningApp.room.RoomEmptyHLDatabase
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.github.HumanLearning2021.HumanLearningApp.view.DownloadSwitchFragment
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@UninstallModules(DatabaseNameModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DownloadSwitchTestDownloadedBeforehand {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @GlobalDatabaseManagement
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @BindValue
    @ProductionDatabaseName
    var dbName = "dummy"

    @Inject
    @RoomDatabase
    lateinit var room: RoomOfflineDatabase

    lateinit var dbMgt: DatabaseManagement
    lateinit var dbDao: DatabaseDao

    @Before
    fun setUp() {
        hiltRule.inject()
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        dbDao = room.databaseDao()
        runBlocking { room.clearAllTables() }
        runBlocking { dbDao.insertAll(RoomEmptyHLDatabase(dbName)) }
        launchFragment()
    }

    @Test
    fun downloadProgressIconInvisibleOnLaunch() {
        Espresso.onView(ViewMatchers.withId(R.id.download_progress_icon))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun switchSet() {
        Espresso.onView(ViewMatchers.withId(R.id.download_switch))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    @Test
    fun switchResetRemovesDatabaseFromDownloads() = runBlocking {
        require(globalDatabaseManagement.getDownloadedDatabases().contains(dbName))
        Espresso.onView(ViewMatchers.withId(R.id.download_switch)).perform(ViewActions.click())
        ViewMatchers.assertThat(
            globalDatabaseManagement.getDownloadedDatabases(), CoreMatchers.not(
                CoreMatchers.hasItem(dbName)
            )
        )
    }

    private fun launchFragment() {
        launchFragmentInHiltContainer<DownloadSwitchFragment>() { }
    }
}