package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
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
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
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
import javax.inject.Inject

@UninstallModules(DatabaseNameModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DisplayImageSetActivityTest {
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

    @BindValue
    @ProductionDatabaseName
    val dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

    @BindValue
    val authPresenter = AuthenticationPresenter(AuthUI.getInstance(), DummyDatabaseService())

    private var dsPictures = emptySet<CategorizedPicture>()
    private lateinit var categories: Set<Category>
    private lateinit var dataset: Dataset
    private lateinit var datasetId: Id
    private var index = 0

    private val navController: NavController = Mockito.mock(NavController::class.java)


    @Before
    fun setUp() {
        hiltRule.inject()  // to get dbManagement set up
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        runBlocking {
            var found = false
            val datasets = dbMgt.getDatasets()
            for (ds in datasets) {
                val dsCats = ds.categories
                if (dsCats.isNotEmpty() && !found) {
                    for (i in dsCats.indices) {
                        val dsPictures = dbMgt.getAllPictures(dsCats.elementAt(i))
                        if (dsPictures.isNotEmpty() && !found) {
                            dataset = ds
                            index = i
                            found = true
                        }
                    }
                }
            }
            if (!found) {
                val cat = dbMgt.putCategory("${UUID.randomUUID()}")
                dataset = dbMgt.putDataset("${UUID.randomUUID()}", setOf(cat))
                val tmp = File.createTempFile("droid", ".png")
                try {
                    ApplicationProvider.getApplicationContext<Context>().resources.openRawResource(R.drawable.fork)
                        .use { img ->
                            tmp.outputStream().use {
                                img.copyTo(it)
                            }
                        }
                    val uri = Uri.fromFile(tmp)
                    dbMgt.putPicture(uri, cat)
                } finally {
                    tmp.delete()
                }
            }
            datasetId = dataset.id
            categories = dataset.categories
            dsPictures = dbMgt.getAllPictures(categories.elementAt(index))
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
        onView(withId(R.id.gridView_display_image_set)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        onView(withId(R.id.textView_display_image_set)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun imageIsDisplayedOnClick() {
        launchFragment()
        assumeTrue(dsPictures.isNotEmpty())
        waitFor(1) // increase if needed
        onView(withId(R.id.gridView_display_image_set)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        onData(CoreMatchers.anything())
            .inAdapterView(withId(R.id.gridView_display_image_set))
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
    fun deletePicturesWorks() {
        launchFragment()
        runBlocking {
            val nbOfPictures = dbMgt.getAllPictures(categories.elementAt(0)).size

            for (i in 0..2) {
                onData(CoreMatchers.anything())
                    .inAdapterView(withId(R.id.gridView_display_image_set))
                    .atPosition(0)
                    .perform(longClick())
            }
            onView(withId(R.id.delete_pictures)).perform(click())
            val nbOfPicturesAfterDelete = dbMgt.getAllPictures(categories.elementAt(0)).size
            assert(nbOfPictures - 1 == nbOfPicturesAfterDelete)
        }
    }

    @Test
    fun setRepresentativePictureWorks() {
        launchFragment()
        runBlocking {
            val reprPicture = dbMgt.getRepresentativePicture(categories.elementAt(0).id)
            val nbOfPictures = dbMgt.getAllPictures(categories.elementAt(0)).size
            onData(CoreMatchers.anything())
                .inAdapterView(withId(R.id.gridView_display_image_set))
                .atPosition(0)
                .perform(longClick())

            onView(withId(R.id.set_representative_picture)).perform(click())
            val reprPictureAfterClick =
                dbMgt.getRepresentativePicture(categories.elementAt(0).id)
            val nbOfPicturesAfterDelete = dbMgt.getAllPictures(categories.elementAt(0)).size
            assert(nbOfPictures - 1 == nbOfPicturesAfterDelete)
            assert(reprPicture != reprPictureAfterClick)
        }
    }

    @Test
    fun clickOnInfoButtonWorks() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.button_start_learning)).perform(click())
            navigateToDisplayImagesetFragment()
            onView(withId(R.id.display_imageset_menu_info)).perform(click())
            waitFor(1) // increase if needed
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(0, 100)
            waitFor(1) // increase if needed
            onView(withId(R.id.gridView_display_image_set)).check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )
        }
    }

    private fun navigateToDisplayImagesetFragment() {
        onView(withId(R.id.datasetsOverviewFragment)).perform(click())
        onView(withId(R.id.recyclerView_dataset_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        onData(anything()).inAdapterView(withId(R.id.gridView_display_dataset_images)).atPosition(0)
            .perform(click())
    }

    private fun launchFragment() {
        val args = bundleOf("datasetId" to datasetId, "category" to categories.elementAt(index))
        launchFragmentInHiltContainer<DisplayImageSetFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}


