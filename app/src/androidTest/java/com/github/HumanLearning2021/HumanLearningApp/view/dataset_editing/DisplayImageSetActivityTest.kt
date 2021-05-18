package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Context
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.GlobalDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
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
    @GlobalDatabaseManagement
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @BindValue
    @ProductionDatabaseName
    var dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

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
        launchFragment()
    }

    @Test
    fun imageSetGridAndNameAreDisplayed() {
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
        Espresso.pressBack()
        verify(navController).popBackStack()
    }

    @Test
    fun deletePicturesWorks() {
        runBlocking {
            val nbOfPictures = dbMgt.getAllPictures(categories.elementAt(0)).size

            for (i in 0..2) {
                onData(CoreMatchers.anything())
                    .inAdapterView(withId(R.id.display_image_set_imagesGridView))
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
        runBlocking {
            val reprPicture = dbMgt.getRepresentativePicture(categories.elementAt(0).id)
            val nbOfPictures = dbMgt.getAllPictures(categories.elementAt(0)).size
            onData(CoreMatchers.anything())
                .inAdapterView(withId(R.id.display_image_set_imagesGridView))
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

    private fun launchFragment() {
        val args = bundleOf("datasetId" to datasetId, "category" to categories.elementAt(index))
        launchFragmentInHiltContainer<DisplayImageSetFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}


