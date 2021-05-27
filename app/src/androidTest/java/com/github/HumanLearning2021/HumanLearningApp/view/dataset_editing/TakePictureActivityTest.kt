package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.Manifest
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.repeatedlyUntil
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.GlobalDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.PermissionGranter
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.Mockito
import org.mockito.Mockito.verify
import java.lang.reflect.Method
import javax.inject.Inject

@UninstallModules(DatabaseNameModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // to enforce consistent order of tests
class TakePictureActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @GlobalDatabaseManagement
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @BindValue
    @ProductionDatabaseName
    var dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

    lateinit var datasetId: String

    private val catSet = setOf<Category>(
        Category("cat1", "cat1"),
        Category("cat2", "cat2"),
        Category("cat3", "cat3"),
    )

    val catSetArray = catSet.toTypedArray()

    private val navController: NavController = Mockito.mock(NavController::class.java)

    private fun grantCameraPermission() {
        PermissionGranter.allowPermissionOneTime(Manifest.permission.CAMERA)
    }

    @Before
    fun setUp() {
        hiltRule.inject()
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        TestUtils.getFirstDataset(dbMgt).id
        datasetId = TestUtils.getFirstDataset(dbMgt).id
        launchFragment()
        // By waiting before the test starts, it allows time for the app to startup to prevent the
        // following error to appear on cirrus:
        // `Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds.`
        // This solution is not ideal because it slows down the tests, and it might not work
        // every time. But there isn't a better solution that I (Niels Lachat) know of.
        val delayBeforeTestStart: Long = 1 // increase if needed
        TestUtils.waitFor(delayBeforeTestStart)
    }


    @Test
    fun correctLayoutIsDisplayAfterCreation() {
        grantCameraPermission()
        assertDisplayed(R.id.cameraPreviewView)
        assertDisplayed(R.id.selectCategoryButton)
        assertDisplayed(R.id.takePictureButton)
        assertDisplayed(R.id.saveButton)
        onView(withId(R.id.cameraImageView)).check(matches(not(isDisplayed())))
    }

    @Test
    fun categoriesCorrectlySetAfterCreation() {
        grantCameraPermission()
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("cat1")).check(matches(isDisplayed()))
        onView(withText("cat2")).check(matches(isDisplayed()))
        onView(withText("cat3")).check(matches(isDisplayed()))
    }


    @Test
    fun correctIntentIsSentOnSave() {
        grantCameraPermission()
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("cat1")).perform(click())
        onView(withId(R.id.takePictureButton)).perform(click())
        onView(withId(R.id.cameraImageView)).perform(
            repeatedlyUntil(
                TestUtils.waitForAction(1000),
                isDisplayed(),
                10
            )
        )
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("cat1")).perform(click())
        waitFor(300)
        onView(withId(R.id.saveButton)).perform(click())
        verify(navController).popBackStack()
    }


    @Test
    fun clickingCaptureButtonShowsPicture() {
        grantCameraPermission()
        onView(withId(R.id.takePictureButton)).perform(click())
        onView(withId(R.id.cameraImageView)).perform(
            repeatedlyUntil(
                TestUtils.waitForAction(1000),
                isDisplayed(),
                10
            )
        )
        onView(withId(R.id.cameraPreviewView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.cameraImageView)).check(matches(isDisplayed()))
    }

    @Test
    fun reclickingCaptureButtonCorrectlyResetsIt() {
        grantCameraPermission()
        onView(withId(R.id.takePictureButton)).perform(click())
        onView(withId(R.id.cameraImageView)).perform(
            repeatedlyUntil(
                TestUtils.waitForAction(1000),
                isDisplayed(),
                10
            )
        )
        onView(withId(R.id.takePictureButton)).perform(click())
        onView(withId(R.id.cameraPreviewView)).check(matches(isDisplayed()))
        onView(withId(R.id.cameraImageView)).check(matches(not(isDisplayed())))
    }

    @Test
    fun saveButtonIsNotClickableIfNoPictureIsTakenAndNoCategorySelected() {
        grantCameraPermission()
        onView(withId(R.id.saveButton)).check(matches(not(isEnabled())))
    }

    @Test
    fun clickingSelectCategoryOpensCorrectDialog() {
        grantCameraPermission()
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("Pick a category")).check(matches(isDisplayed()))
    }

    @Test
    fun previewViewIsNotClickable() {
        grantCameraPermission()
        onView(withId(R.id.cameraPreviewView)).check(matches(not(isClickable())))
    }

    @Test
    fun takePictureIsClickable() {
        grantCameraPermission()
        onView(withId(R.id.takePictureButton)).check(matches(isClickable()))
    }

    private val delayAfterSelectCategoryBtn = 100L

    @Test
    fun selectingCategoryChangesButtonText() {
        grantCameraPermission()
        onView(withId(R.id.selectCategoryButton)).perform(click())
        TestUtils.waitFor(delayAfterSelectCategoryBtn)
        onView(withText("cat1")).perform(click())
        onView(withId(R.id.selectCategoryButton)).check(matches(withText("cat1")))
    }

    @Test
    fun selectingCategoryChangesButtonTextColor() {
        grantCameraPermission()
        onView(withId(R.id.selectCategoryButton)).perform(click())
        TestUtils.waitFor(delayAfterSelectCategoryBtn)
        onView(withText("cat1")).perform(click())
        onView(withId(R.id.selectCategoryButton)).check(matches(hasTextColor(R.color.black)))
    }


    /*
    TODO: convert to fragment

    @Test
    fun receiveIntentFromCamera() {
        Intents.init()
        val imageUri =
            Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.knife)
        Intents.intending(hasComponent(TakePictureFragment::class.qualifiedName)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                Intent().putExtra(
                    "result",
                    bundleOf("category" to DummyCategory("cat1", "cat1"), "image" to imageUri)
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.use_camera))
            .perform(ViewActions.click())
        val result = testRule.scenario.result
        MatcherAssert.assertThat(result.resultCode, Matchers.equalTo(Activity.RESULT_OK))
        MatcherAssert.assertThat(result.resultData, IntentMatchers.hasExtraWithKey("result"))
        Intents.release()
    }
     */


    @Test
    fun permissionNeededDialogShowsCorrectDialog() {
        grantCameraPermission()
        launchFragmentWithPermission()
        onView(withText("Camera required")).inRoot(RootMatchers.isDialog()).check(
            matches(
                isDisplayed()
            )
        )
    }


    @Test
    fun showCaptureErrorDialogShowsCorrectly() {
        grantCameraPermission()
        launchFragmentWithErrorDialog()
        onView(withText("Error")).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
    }


    @Test
    fun backButtonWorks() {
        Espresso.pressBack()
        Mockito.verify(navController).popBackStack()
    }

    private fun launchFragment() {
        val args = bundleOf("categories" to catSetArray, "datasetId" to datasetId)
        launchFragmentInHiltContainer<TakePictureFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    private fun launchFragmentWithPermission() {
        val args = bundleOf("categories" to catSetArray, "datasetId" to datasetId)
        launchFragmentInHiltContainer<TakePictureFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
            val method: Method =
                TakePictureFragment::class.java.getDeclaredMethod("permissionNeededDialog")
            method.isAccessible = true
            method.invoke(this)
        }
    }


    private fun launchFragmentWithErrorDialog() {
        val args = bundleOf("categories" to catSetArray, "datasetId" to datasetId)
        launchFragmentInHiltContainer<TakePictureFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
            val method: Method =
                TakePictureFragment::class.java.getDeclaredMethod("showCaptureErrorDialog")
            method.isAccessible = true
            method.invoke(this)
        }
    }

    /*
    private fun launchFragmentForResult() {
        val args = bundleOf("categories" to catSetArray, "datasetId" to datasetId)
        launchFragmentInHiltContainer<TakePictureFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
            this.parentFragmentManager.setFragmentResult(AddPictureFragment.REQUEST_KEY, bundleOf("chosenCategory" to chosenCategory, "pictureUri" to pictureUri))
            assertThat(fragment.result).isEqualTo(expectedResult)

        }
    }

     */

}
