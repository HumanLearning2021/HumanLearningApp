package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.content.Context
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import java.io.File
import java.util.*


@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LearningDatasetSelectionTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue @Demo2Database
    val dbManagement: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private var datasetPictures = emptySet<CategorizedPicture>()
    private var categories = emptySet<Category>()
    private lateinit var dataset: Dataset
    private lateinit var datasetId: String
    private var index = 0

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        runBlocking {
            var found = false
            val datasets = dbManagement.getDatasets()
            for (ds in datasets) {
                val dsCats = ds.categories
                if (dsCats.isNotEmpty() && !found) {
                    for (i in dsCats.indices) {
                        val dsPictures = dbManagement.getAllPictures(dsCats.elementAt(i))
                        if (dsPictures.isNotEmpty() && !found) {
                            dataset = ds
                            index = i
                            found = true
                        }
                    }
                }
            }
            if (!found) {
                val cat = dbManagement.putCategory("${UUID.randomUUID()}")
                dataset = dbManagement.putDataset("${UUID.randomUUID()}", setOf(cat))
                val tmp = File.createTempFile("droid", ".png")
                try {
                    ApplicationProvider.getApplicationContext<Context>().resources.openRawResource(R.drawable.fork).use { img ->
                        tmp.outputStream().use {
                            img.copyTo(it)
                        }
                    }
                    val uri = Uri.fromFile(tmp)
                    dbManagement.putPicture(uri, cat)
                } finally {
                    tmp.delete()
                }
            }
            categories = emptySet()
            datasetPictures = emptySet()
            datasetId = dataset.id as String
        }
    }

    @Test
    fun allViewsAreDisplayed(){
        launchFragment()
        assertDisplayed(R.id.LearningDatasetSelection_dataset_list)
    }


    @Test
    fun clickingADatasetLaunchesLearningSettings(){
        launchFragment()

        onView(ViewMatchers.withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        verify(navController).navigate(
            LearningDatasetSelectionFragmentDirections.actionLearningDatasetSelectionFragmentToLearningSettingsFragment(datasetId)
        )
    }

    private fun launchFragment(){
        launchFragmentInHiltContainer<LearningDatasetSelectionFragment>() {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}

