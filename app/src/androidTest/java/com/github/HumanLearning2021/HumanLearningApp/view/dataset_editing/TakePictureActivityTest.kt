package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.repeatedlyUntil
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.PermissionGranter
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.Mockito
import org.mockito.Mockito.verify
import java.lang.reflect.Method

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // to enforce consistent order of tests
class TakePictureActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbManagement: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private val datasetId: String = TestUtils.getFirstDataset(dbManagement).id

    private val catSet = setOf<Category>(
        DummyCategory("cat1", "cat1"),
        DummyCategory("cat2", "cat2"),
        DummyCategory("cat3", "cat3"),
    )

    val catSetArray = catSet.toTypedArray()

    private val navController: NavController = Mockito.mock(NavController::class.java)

    private fun grantCameraPermission() {
        PermissionGranter.allowPermissionOneTime(Manifest.permission.CAMERA)
    }

    @Parcelize
    private class TestCat(
        override val id: String, override val name: String
    ) : Category


    @Before
    fun setUp() {
        hiltRule.inject()
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
        onView(withId(R.id.saveButton)).perform(click())
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("cat1")).perform(click())
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
    Don't know how to test this without FragmentScenario

    @Test
    fun receiveIntentFromCamera() {
        val imageUri =
            Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.knife)
        Intents.intending(hasComponent(TakePictureActivity::class.qualifiedName)).respondWith(
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
    }
    */



    @Test
    fun permissionNeededDialogShowsCorrectDialog() {
        grantCameraPermission()
        val method: Method =
            TakePictureFragment::class.java.getDeclaredMethod("permissionNeededDialog")
        method.isAccessible = true
        launchFragmentWithPermission()

        onView(withText("Camera required")).inRoot(RootMatchers.isDialog()).check(
            matches(
                isDisplayed()
            )
        )
    }

    /*
    @Test
    fun showCaptureErrorDialogShowsCorrectly() {
        grantCameraPermission()
        val method: Method =
            TakePictureFragment::class.java.getDeclaredMethod("showCaptureErrorDialog")
        method.isAccessible = true
        activityScenarioRule.scenario.onActivity { activity ->
            method.invoke(activity)
        }
        onView(withText("Error")).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
    }

     */


    @Test
    fun backButtonWorks(){
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
            method.invoke(this)
        }
    }



}
