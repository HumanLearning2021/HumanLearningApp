package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.repeatedlyUntil
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.PermissionGranter
import kotlinx.parcelize.Parcelize
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.lang.reflect.Method

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // to enforce consistent order of tests
class AddPictureActivityTest {

    private fun grantCameraPermission() {
        PermissionGranter.allowPermissionOneTime(Manifest.permission.CAMERA)
    }

    @Parcelize
    private class TestCat(
        override val id: String, override val name: String
    ) : Category

    private val catSet = setOf<Category>(
        TestCat("cat1", "cat1"),
        TestCat("cat2", "cat2"),
        TestCat("cat3", "cat3")
    )

    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<AddPictureActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            AddPictureActivity::class.java
        ).putExtra("categories", ArrayList<Category>(catSet))
    )

    @Before
    fun setUp() {
        Intents.init()
        // By waiting before the test starts, it allows time for the app to startup to prevent the
        // following error to appear on cirrus:
        // `Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds.`
        // This solution is not ideal because it slows down the tests, and it might not work
        // every time. But there isn't a better solution that I (Niels Lachat) know of.
        val delayBeforeTestStart: Long = 1 // increase if needed
        TestUtils.waitFor(delayBeforeTestStart)
    }


    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
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
        assert(activityScenarioRule.scenario.result.resultCode == Activity.RESULT_OK)
        assert(activityScenarioRule.scenario.result.resultData.hasExtra("result"))
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

    @Test
    fun permissionNeededDialogShowsCorrectDialog() {
        grantCameraPermission()
        val method: Method =
            AddPictureActivity::class.java.getDeclaredMethod("permissionNeededDialog")
        method.isAccessible = true
        activityScenarioRule.scenario.onActivity { activity ->
            method.invoke(activity)
        }
        onView(withText("Camera required")).inRoot(RootMatchers.isDialog()).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun showCaptureErrorDialogShowsCorrectly() {
        grantCameraPermission()
        val method: Method =
            AddPictureActivity::class.java.getDeclaredMethod("showCaptureErrorDialog")
        method.isAccessible = true
        activityScenarioRule.scenario.onActivity { activity ->
            method.invoke(activity)
        }
        onView(withText("Error")).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun activityContractCorrectlyParsesResult() {
        val bundle = Bundle().apply {
            putParcelable("category", TestCat("some_category", "some_category"))
            putParcelable("image", Uri.EMPTY)
        }
        val intent = Intent().putExtra("result", bundle)
        assert(
            AddPictureActivity.AddPictureContract.parseResult(
                Activity.RESULT_OK,
                intent
            )!!.first.name == "some_category"
        )
        assert(
            AddPictureActivity.AddPictureContract.parseResult(
                Activity.RESULT_CANCELED,
                intent
            ) == null
        )
    }

}
