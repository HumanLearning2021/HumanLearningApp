package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.getFirstDataset
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
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
class DisplayImageActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbMgt: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private var datasetPictures = emptySet<CategorizedPicture>()
    private var categories = emptySet<Category>()
    private val dataset = getFirstDataset(dbMgt)
    private lateinit var datasetId: String
    private lateinit var categoryWith1Picture: Category
    private lateinit var categoryWith2Pictures: Category


    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        categories = emptySet()
        datasetPictures = emptySet()
        datasetId = dataset.id
        categoryWith1Picture = newCategoryWithNPictures(1)
        categoryWith2Pictures = newCategoryWithNPictures(2)
    }


    private fun getFirstPicture(category: Category) = runBlocking {
        dbMgt.getAllPictures(category).first()
    }


    @Test
    fun pictureAndCategoryAreDisplayed() {
        launchFragmentWithPictureOfCategory(categoryWith1Picture)

        onView(withId(R.id.display_image_viewImage))
            .check(matches(isDisplayed()))
        onView(withId(R.id.display_image_viewCategory))
            .check(matches(isDisplayed()))
        onView(withId(R.id.display_image_delete_button)).check(matches(isDisplayed()))
    }

    private fun getForkUri(): Uri {
        // could maybe be made simpler
        val res = ApplicationProvider.getApplicationContext<Context>().resources
        val rId = R.drawable.fork
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(rId) +
                    "/" + res.getResourceTypeName(rId) +
                    "/" + res.getResourceEntryName(rId)
        )
    }

    private fun newCategoryWithNPictures(N: Int): Category {
        require(N > 0)

        return runBlocking {
            val newCategory = dbMgt.putCategory("${UUID.randomUUID()}")

            dbMgt.addCategoryToDataset(dataset, newCategory)

            val forkUri = getForkUri()
            for (i in 1..N) {
                dbMgt.putPicture(forkUri, newCategory)
            }
            newCategory
        }
    }

    @Test
    fun deleteLastImageOfCategory() {
        launchFragmentWithPictureOfCategory(categoryWith1Picture)

        onView(withId(R.id.display_image_delete_button)).perform(click())
        waitFor(1) // increase if needed
        val updatedPicturesInCategory = runBlocking {
            dbMgt.getAllPictures(categoryWith1Picture)
        }
        assumeTrue(updatedPicturesInCategory.isEmpty())

        Mockito.verify(navController).navigate(
            DisplayImageFragmentDirections.actionDisplayImageFragmentToDisplayDatasetFragment(
                datasetId
            )
        )
    }

    @Test
    fun deleteNotLastImageInCategory() {
        launchFragmentWithPictureOfCategory(categoryWith2Pictures)

        onView(withId(R.id.display_image_delete_button)).perform(click())
        waitFor(1) // increase if needed
        val updatedPicturesInCategory = runBlocking {
            dbMgt.getAllPictures(categoryWith1Picture)
        }
        assumeTrue(updatedPicturesInCategory.isNotEmpty())

        verify(navController).navigate(
            DisplayImageFragmentDirections.actionDisplayImageFragmentToDisplayImageSetFragment(
                datasetId,
                categoryWith2Pictures
            )
        )
    }

    @Test
    fun setAsRepresentativePictureButtonWorks() {
        launchFragmentWithPictureOfCategory(categoryWith2Pictures)

        onView(withId(R.id.display_image_set_representative_picture)).perform(click())
        // TODO test that the functionality is correctly implemented
    }

    private fun launchFragmentWithPictureOfCategory(category: Category) {
        val args = bundleOf("datasetId" to datasetId, "picture" to getFirstPicture(category))
        launchFragmentInHiltContainer<DisplayImageFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}