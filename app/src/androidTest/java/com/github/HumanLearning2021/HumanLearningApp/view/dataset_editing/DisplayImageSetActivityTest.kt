package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.anything
import org.junit.After
import org.junit.Assume.assumeTrue
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
class DisplayImageSetActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
    )

    @BindValue
    @Demo2Database
    val dbManagement: DatabaseManagement = DefaultDatabaseManagement(DummyDatabaseService())

    private var dsPictures = emptySet<CategorizedPicture>()
    private lateinit var categories: Set<Category>
    private lateinit var dataset: Dataset
    private lateinit var datasetId: Id
    private var index = 0

    private val navController: NavController = Mockito.mock(NavController::class.java)


    @Before
    fun setUp() {
        hiltRule.inject()  // to get dbManagement set up
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
                    ApplicationProvider.getApplicationContext<Context>().resources.openRawResource(R.drawable.fork)
                        .use { img ->
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
            datasetId = dataset.id
            categories = dataset.categories
            dsPictures = dbManagement.getAllPictures(categories.elementAt(index))
        }
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }

    @Test
    fun imageSetGridAndNameAreDisplayed() {
        launchFragment()
        assumeTrue(dsPictures.isNotEmpty())
        onView(withId(R.id.display_image_set_imagesGridView)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        onView(withId(R.id.display_image_set_name)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun imageIsDisplayedOnClick() {
        launchFragment()
        assumeTrue(dsPictures.isNotEmpty())
        waitFor(1) // increase if needed
        onView(withId(R.id.display_image_set_imagesGridView)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        onData(CoreMatchers.anything())
            .inAdapterView(withId(R.id.display_image_set_imagesGridView))
            .atPosition(0)
            .perform(click())


        verify(navController).navigate(
            DisplayImageSetFragmentDirections.actionDisplayImageSetFragmentToDisplayImageFragment(
                dsPictures.elementAt(0),
                datasetId
            )
        )
    }

    @Test
    fun onBackPressedWorks() {
        launchFragment()
        Espresso.pressBack()
        verify(navController).popBackStack()
    }

    @Test
    fun deletePicturesWorks() {
        launchFragment()
        runBlocking {
            val nbOfPictures = dbManagement.getAllPictures(categories.elementAt(0)).size

            for (i in 0..2) {
                onData(CoreMatchers.anything())
                    .inAdapterView(withId(R.id.display_image_set_imagesGridView))
                    .atPosition(0)
                    .perform(longClick())
            }
            onView(withId(R.id.delete_pictures)).perform(click())
            val nbOfPicturesAfterDelete = dbManagement.getAllPictures(categories.elementAt(0)).size
            assert(nbOfPictures - 1 == nbOfPicturesAfterDelete)
        }
    }

    @Test
    fun setRepresentativePictureWorks() {
        launchFragment()
        runBlocking {
            val reprPicture = dbManagement.getRepresentativePicture(categories.elementAt(0).id)
            val nbOfPictures = dbManagement.getAllPictures(categories.elementAt(0)).size
            onData(CoreMatchers.anything())
                .inAdapterView(withId(R.id.display_image_set_imagesGridView))
                .atPosition(0)
                .perform(longClick())

            onView(withId(R.id.set_representative_picture)).perform(click())
            val reprPictureAfterClick =
                dbManagement.getRepresentativePicture(categories.elementAt(0).id)
            val nbOfPicturesAfterDelete = dbManagement.getAllPictures(categories.elementAt(0)).size
            assert(nbOfPictures - 1 == nbOfPicturesAfterDelete)
            assert(reprPicture != reprPictureAfterClick)
        }
    }

    @Test
    fun clickOnInfoButtonWorks() {
        navigateToDisplayImagesetFragment()
        onView(withId(R.id.display_imageset_menu_info)).perform(click())
        waitFor(1) // increase if needed
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(0, 100)
        waitFor(1) // increase if needed
        onView(withId(R.id.display_image_set_imagesGridView)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    private fun navigateToDisplayImagesetFragment() {
        onView(withId(R.id.datasetsOverviewFragment)).perform(click())
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        onData(anything()).inAdapterView(withId(R.id.display_dataset_imagesGridView)).atPosition(0)
            .perform(click());
    }

    private fun launchFragment() {
        val args = bundleOf("datasetId" to datasetId, "category" to categories.elementAt(index))
        launchFragmentInHiltContainer<DisplayImageSetFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}


