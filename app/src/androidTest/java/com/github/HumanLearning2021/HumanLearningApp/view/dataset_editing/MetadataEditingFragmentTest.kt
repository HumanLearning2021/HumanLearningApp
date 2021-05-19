package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.getFirstDataset
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.withIndex
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.hasSize
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MetadataEditingFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbMgt: DatabaseManagement = DefaultDatabaseManagement(DummyDatabaseService())

    private var dataset: Dataset = getFirstDataset(dbMgt)
    private val datasetId: String = getFirstDataset(dbMgt).id

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        launchFragment()
    }

    @Test
    fun rowViewIsDisplayedWhenAddButtonIsClicked() {
        onView(withId(R.id.button_add)).perform(click())
        onView(withText("")).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun rowButtonViewIsDisplayedWhenAddButtonIsClicked() {
        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.button_add)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun rowViewIsAddedWhenAddButtonIsClicked() {
        onView(withId(R.id.button_add)).perform(click())
        waitFor(1) // increase if needed
        onView(withId(R.id.parent_linear_layout)).check(
            ViewAssertions.matches(
                hasChildCount(dataset.categories.size + 1)
            )
        )
    }

    @Test
    fun rowViewIsRemovedWhenRemoveButtonIsClicked() {
        runBlocking {
            waitFor(1) // increase if needed
            val nbCategories = dataset.categories.size
            assumeTrue(dataset.categories.isNotEmpty())

            // click on first remove button
            onView(withIndex(withId(R.id.button_remove), 0)).perform(click())

            onView(withId(R.id.parent_linear_layout)).check(
                ViewAssertions.matches(
                    hasChildCount(nbCategories - 1)
                )
            )

            val updatedDataset = dbMgt.getDatasetById(dataset.id)!!
            assertThat(updatedDataset.categories, hasSize(nbCategories - 1))
        }
    }

    @Test
    fun saveButtonGoesToDisplayDatasetActivity() {
        onView(withId(R.id.button_submit_list)).perform(click())
        waitFor(1) // increase if needed
        verify(navController).navigate(
            MetadataEditingFragmentDirections.actionCategoriesEditingFragmentToDisplayDatasetFragment(
                datasetId
            )
        )
    }

    @Test
    fun addNewCategoryToDatasetWorks() {
        val nbCategories = dataset.categories.size
        onView(withId(R.id.button_add)).perform(click())
        onView(withText("")).perform(typeText("new beautiful category"))
        closeSoftKeyboard()
        onView(withId(R.id.button_submit_list)).perform(click())
        waitFor(1) // increase id needed
        runBlocking {
            val updatedDataset = dbMgt.getDatasetById(datasetId)!!
            assert(nbCategories + 1 == updatedDataset.categories.size)
        }
    }

    @Test
    fun backButtonWorks() {
        Espresso.pressBack()
        verify(navController).popBackStack()
    }

    @Test
    fun modifyingDatasetNameWorks() {
        val newName = "new dataset name"
        runBlocking {
            waitFor(1) // increase if needed
            onView(withId(R.id.dataset_name)).perform(
                ViewActions.clearText(),
                typeText("$newName\n")
            )
            onView(withId(R.id.dataset_name)).check(
                ViewAssertions.matches(
                    withText(
                        CoreMatchers.containsString(newName)
                    )
                )
            )

            // need to get again because dataset is immutable and editing the name creates a new
            // Dataset object in the database
            dataset = dbMgt.getDatasetById(datasetId)!!
            assert(dataset.name == newName) {
                "dataset name \"${dataset.name}\" different" +
                        " from \"$newName\""
            }
        }
    }

    private fun launchFragment() {
        val args = bundleOf("datasetId" to datasetId)
        launchFragmentInHiltContainer<MetadataEditingFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}

