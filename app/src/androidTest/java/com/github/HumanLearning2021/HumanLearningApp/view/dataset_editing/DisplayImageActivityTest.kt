package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.getFirstDataset
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject

@UninstallModules(DatabaseNameModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DisplayImageActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @BindValue
    @ProductionDatabaseName
    val dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

    private var datasetPictures = emptySet<CategorizedPicture>()
    private var categories = emptySet<Category>()
    lateinit var dataset: Dataset
    lateinit var datasetId: String
    private lateinit var categoryWith1Picture: Category
    private lateinit var categoryWith2Pictures: Category


    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        dataset = getFirstDataset(dbMgt)
        datasetId = dataset.id
        categories = emptySet()
        datasetPictures = emptySet()
        categoryWith1Picture = newCategoryWithNPictures(1)
        categoryWith2Pictures = newCategoryWithNPictures(2)
    }


    private fun getFirstPicture(category: Category) = runBlocking {
        dbMgt.getAllPictures(category).first()
    }


    @Test
    fun pictureAndCategoryAreDisplayed() {
        launchFragmentWithPictureOfCategory(categoryWith1Picture)

        onView(withId(R.id.imageView_display_image))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textView_display_image))
            .check(matches(isDisplayed()))
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

            dataset = dbMgt.getDatasetById(datasetId)!!
            dbMgt.addCategoryToDataset(dataset, newCategory)

            val forkUri = getForkUri()
            for (i in 1..N) {
                dbMgt.putPicture(forkUri, newCategory)
            }
            newCategory
        }
    }

    private fun launchFragmentWithPictureOfCategory(category: Category) {
        val args = bundleOf("datasetId" to datasetId, "picture" to getFirstPicture(category))
        launchFragmentInHiltContainer<DisplayImageFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }

}
