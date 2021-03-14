package com.github.HumanLearning2021.HumanLearningApp

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.PermissionGranter
import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
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

    @get:Rule
    var testActivity = ActivityTestRule(AddPictureActivity::class.java)
    @get:Rule
    var preferenceResetRule = ClearPreferencesRule()

    @Before
    fun setUp() {
        Intents.init()
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