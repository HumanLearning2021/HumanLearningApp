package com.github.HumanLearning2021.HumanLearningApp

import android.Manifest
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.PermissionGranter
import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // to enforce consistent order of tests
class AddPictureActivityTest {

    private fun denyPermission() {
        val instrumentation = getInstrumentation()
        if (Build.VERSION.SDK_INT >= 23) {
            val denyPermission = UiDevice.getInstance(instrumentation).findObject(UiSelector().text(
                    when (Build.VERSION.SDK_INT) {
                        in 24..28 -> "DENY"
                        else -> "Deny"
                    }
            ))
            if (denyPermission.exists()) {
                denyPermission.click()
            }
        }
    }

    private fun grantPermission() {
        PermissionGranter.allowPermissionOneTime(Manifest.permission.CAMERA)
        testActivity.activity.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
    }

    // inspired by : https://stackoverflow.com/a/35924943/7158887
    private fun waitFor(millis: Long): ViewAction =
            object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return isRoot()
                }

                override fun getDescription(): String {
                    return "Wait for $millis milliseconds"
                }

                override fun perform(uiController: UiController?, view: View?) {
                    uiController?.loopMainThreadForAtLeast(millis)
                }
            }


    @get:Rule
    var testActivity = ActivityTestRule(AddPictureActivity::class.java)

    @get:Rule
    var preferenceResetRule = ClearPreferencesRule()

    @Before
    fun setUp() {
        Intents.init()
        // By waiting before the test starts, it allows time for the app to startup to prevent the
        // following error to appear on cirrus:
        // `Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds.`
        // This solution is not ideal because it slows down the tests, and it might not work
        // every time. But there isn't a better solution that I (Niels Lachat) know of.
        val delayBeforeTestStart: Long = 3000
        onView(isRoot()).perform(waitFor(delayBeforeTestStart))
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun cameraPermissionIsRequestedOnLaunch() {
        grantPermission()
        assertDisplayed(R.id.cameraPreviewView)
    }


    @Test
    fun saveButtonIsNotClickableIfNoPictureIsTakenAndNoCategorySelected() {
        grantPermission()
        onView(withId(R.id.saveButton)).check(matches(not(isEnabled())))
    }

    @Test
    fun clickingSelectCategoryOpensCorrectDialog() {
        grantPermission()
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("Pick a category")).check(matches(isDisplayed()))
    }

    @Test
    fun previewViewIsNotClickable() {
        grantPermission()
        onView(withId(R.id.cameraPreviewView)).check(matches(not(isClickable())))
    }

    @Test
    fun takePictureIsClickable() {
        grantPermission()
        onView(withId(R.id.takePictureButton)).check(matches(isClickable()))
    }

    @Test
    fun selectingCategoryChangesButtonText() {
        grantPermission()
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("category1")).perform(click())
        onView(withId(R.id.selectCategoryButton)).check(matches(withText("category1")))
    }

    @Test
    fun selectingCategoryChangesButtonTextColor() {
        grantPermission()
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("category1")).perform(click())
        onView(withId(R.id.selectCategoryButton)).check(matches(hasTextColor(R.color.black)))
    }
}