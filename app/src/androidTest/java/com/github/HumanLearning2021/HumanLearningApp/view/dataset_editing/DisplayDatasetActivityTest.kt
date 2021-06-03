package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.Manifest
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.schibsted.spain.barista.interaction.PermissionGranter
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers.anything
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
class DisplayDatasetActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
    )

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @BindValue
    @ProductionDatabaseName
    val dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

    @BindValue
    val authPresenter = AuthenticationPresenter(AuthUI.getInstance(), DummyDatabaseService())

    private var datasetPictures = emptySet<CategorizedPicture>()
    private var categories = emptySet<Category>()
    private lateinit var dataset: Dataset
    private var index = 0

    private val mockNavController: NavController = Mockito.mock(NavController::class.java)

    private val datasetId = "kitchen utensils"

    @Before
    fun setup() {
        PermissionGranter.allowPermissionOneTime(Manifest.permission.CAMERA)
        hiltRule.inject()  // ensures dbManagement is available
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
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


    /**
     * Check that the Grid with all the images of the dataset are displayed.
     */
    @Test
    fun datasetGridAndNameAreDisplayed() {
        launchFragment()
        onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
        onView(withId(R.id.display_dataset_name)).check(matches(isDisplayed()))
    }

    /**
     * Check that when an image is clicked, DisplayImageActivity is launched with the good data in it.
     */
    @ExperimentalCoroutinesApi
    @Test
    fun whenClickOnCategoryImageDisplayImageSetActivity() {
        launchFragment()
        runBlocking {
            categories = dataset.categories
            for (cat in categories) {
                datasetPictures = datasetPictures.plus(dbMgt.getAllPictures(cat))
            }
            waitFor(1) // increase if needed
            assumeTrue(datasetPictures.isNotEmpty())

            onData(anything())
                .inAdapterView(withId(R.id.display_dataset_imagesGridView))
                .atPosition(0)
                .perform(click())

            verify(
                mockNavController
            ).navigate(
                DisplayDatasetFragmentDirections.actionDisplayDatasetFragmentToDisplayImageSetFragment(
                    datasetId, categories.elementAt(
                        index
                    )
                )
            )
        }
    }

    private fun navigateToDisplayDatasetFragment() {
        onView(withId(R.id.datasetsOverviewFragment)).perform(click())
        onView(withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )
    }

    @Test
    fun clickOnMenuModifyCategoriesWorks() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.startLearningButton)).perform(click())
            navigateToDisplayDatasetFragment()
            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            onView(withText("Modify name and categories")).perform(click())
            activityScenarioRule.scenario.onActivity {
                val currentFragment =
                    it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
                assert(currentFragment?.findNavController()?.currentDestination?.id == R.id.categoriesEditingFragment)
            }
        }
    }

    @Test
    fun clickOnMenuAddNewPictureWorks() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.startLearningButton)).perform(click())
            navigateToDisplayDatasetFragment()
            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            onView(withText(R.string.add_new_picture)).perform(click())
            onView(withText(R.string.use_camera)).perform(click())

            activityScenarioRule.scenario.onActivity {
                val currentFragment =
                    it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
                assert(currentFragment?.findNavController()?.currentDestination?.id == R.id.takePictureFragment)
            }

            categories = dataset.categories
            assumeTrue(categories.isNotEmpty())

            val numberOfPictures = dbMgt.getAllPictures(categories.elementAt(index)).size
            onView(withId(R.id.takePictureButton)).perform(click())
            onView(withId(R.id.selectCategoryButton)).perform(click())
            onView(withText(categories.elementAt(index).name)).perform(click())
            waitFor(300) // increase if needed
            onView(withId(R.id.saveButton)).perform(click())
            waitFor(150) // increase if needed
            onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
            assert(dbMgt.getAllPictures(categories.elementAt(index)).size == numberOfPictures + 1)

        }
    }

    @Test
    fun clickOnMenuDeleteDatasetWorks() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.startLearningButton)).perform(click())
            navigateToDisplayDatasetFragment()
            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            onView(withText(R.string.delete_dataset)).perform(click())
            onView(withText("No")).perform(click())


            val numberOfDatasets = dbMgt.getDatasets().size

            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            onView(withText(R.string.delete_dataset)).perform(click())
            onView(withText("Yes")).perform(click())

            activityScenarioRule.scenario.onActivity {
                val currentFragment =
                    it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
                assert(currentFragment?.findNavController()?.currentDestination?.id == R.id.datasetsOverviewFragment)
            }

            val numberOfDatasetsAfter = dbMgt.getDatasets().size
            assert(numberOfDatasets - 1 == numberOfDatasetsAfter)

        }
    }

    @Test
    fun clickOnMenuButNotOnButtonClosesMenu() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.startLearningButton)).perform(click())
            navigateToDisplayDatasetFragment()
            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            UiDevice.getInstance(getInstrumentation()).click(0, 100)
            onView(withId(R.id.display_dataset_imagesGridView)).check(matches(isDisplayed()))
        }
    }

    private fun launchFragment() {
        val args = bundleOf("datasetId" to datasetId)
        launchFragmentInHiltContainer<DisplayDatasetFragment>(args) {
            Navigation.setViewNavController(requireView(), mockNavController)
        }
    }
}
