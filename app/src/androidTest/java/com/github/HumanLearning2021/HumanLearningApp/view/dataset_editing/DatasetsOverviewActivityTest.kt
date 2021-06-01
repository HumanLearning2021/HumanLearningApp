package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.getFirstDataset
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import javax.inject.Inject


@UninstallModules(DatabaseNameModule::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DatasetsOverviewActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @BindValue
    @ProductionDatabaseName
    val dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

    lateinit var datasetId: String
    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        datasetId = getFirstDataset(dbMgt).id
        launchFragment()
    }

    @Test
    fun fragmentIsDisplayedWhenActivityIsLaunched() {
        assertDisplayed(R.id.datasetListFragment)
        assertDisplayed(R.id.createDatasetButton)
    }

    @Test
    fun rightActivityIsStartedAfterCreateButtonIsClicked() {
        onView(withId(R.id.createDatasetButton)).perform(click())
        verify(navController).navigate(
            DatasetsOverviewFragmentDirections.actionDatasetsOverviewFragmentToCategoriesEditingFragment(
                null
            )
        )
    }

    @Test
    fun whenClickOnDatasetDisplayDatasetActivity() {
        onView(withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )

        verify(navController).navigate(
            DatasetsOverviewFragmentDirections.actionDatasetsOverviewFragmentToDisplayDatasetFragment(
                datasetId
            )
        )
    }
    
    private fun launchFragment() {
        val args = bundleOf("datasetId" to datasetId)
        launchFragmentInHiltContainer<DatasetsOverviewFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}
