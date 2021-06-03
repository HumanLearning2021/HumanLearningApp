package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.getFirstDataset
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.withIndex
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseServiceModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import javax.inject.Inject

@UninstallModules(DatabaseNameModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MetadataEditingFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
    )

    private val dbService = DatabaseServiceModule.provideDummyService()

    @BindValue
    @ProductionDatabaseName
    val dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

    @BindValue
    val authPresenter = AuthenticationPresenter(AuthUI.getInstance(), DummyDatabaseService())

    lateinit var dataset: Dataset
    lateinit var datasetId: String

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        dataset = getFirstDataset(dbMgt)
        datasetId = getFirstDataset(dbMgt).id
        runBlocking {
            val ds = dbMgt.getDatasetById(datasetId)
            require(ds != null) {
                "The dataset with id $datasetId doesn't exist in the database. Fix this"
            }
            dataset = ds
        }
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }

    @Test
    fun rowViewIsDisplayedWhenAddButtonIsClicked() {
        launchFragment()
        onView(withId(R.id.button_add)).perform(click())
        onView(withText("")).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun rowButtonViewIsDisplayedWhenAddButtonIsClicked() {
        launchFragment()
        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.button_add)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun rowViewIsAddedWhenAddButtonIsClicked() {
        launchFragment()
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
        launchFragment()
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
        launchFragment()
        onView(withId(R.id.button_submit_list)).perform(click())
        waitFor(1) // increase if needed
        verify(navController).navigate(
            MetadataEditingFragmentDirections.actionCategoriesEditingFragmentToDisplayDatasetFragment(
                datasetId
            )
        )
    }

    @Test
    fun clickOnInfoButtonWorks() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.startLearningButton)).perform(click())
            navigateToCreateDatasetFragment()
            onView(withId(R.id.categories_editing_menu_info)).perform(click())
            waitFor(1) // increase if needed
            onView(
                withText(
                    ApplicationProvider.getApplicationContext<Context>()
                        .getString(R.string.MetadataEditing_infoTitle)
                )
            ).check(
                ViewAssertions.matches(isDisplayed())
            )
            pressBack()
            waitFor(1) // increase if needed
            onView(withId(R.id.button_add)).check(ViewAssertions.matches(isDisplayed()))
        }
    }

    @Test
    fun addNewCategoryToDatasetWorks() {
        launchFragment()
        val nbCategories = dataset.categories.size
        onView(withId(R.id.button_add)).perform(click())
        onView(withText("")).perform(typeText("new beautiful category"))
        closeSoftKeyboard()
        onView(withId(R.id.button_submit_list)).perform(click())
        waitFor(1) // increase if needed
        runBlocking {
            val updatedDataset = dbMgt.getDatasetById(datasetId)!!
            assert(nbCategories + 1 == updatedDataset.categories.size)
        }
    }

    @Test
    fun modifyingDatasetNameWorks() {
        launchFragment()
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

    private fun navigateToCreateDatasetFragment() {
        onView(withId(R.id.datasetsOverviewFragment)).perform(click())
        onView(withId(R.id.createDatasetButton)).perform(click())
    }
}

