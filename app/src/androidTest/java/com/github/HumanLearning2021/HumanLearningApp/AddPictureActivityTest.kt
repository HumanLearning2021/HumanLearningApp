package com.github.HumanLearning2021.HumanLearningApp

import android.Manifest
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.widget.Button
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.PermissionGranter
import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AddPictureActivityTest {

    @get:Rule
    var testRule = ActivityTestRule(AddPictureActivity::class.java)
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
        PermissionGranter.allowPermissionOneTime(Manifest.permission.CAMERA)
        assertDisplayed(R.id.cameraPreviewView)
    }


    @Test
    fun saveButtonIsNotClickableIfNoPictureIsTakenAndNoCategorySelected() {
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.CAMERA)
        onView(withId(R.id.saveButton)).check(matches(not(isEnabled())))
    }

    @Test
    fun clickingSelectCategoryOpensCorrectDialog() {
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.CAMERA)
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("Pick a category")).check(matches(isDisplayed()))
    }

    @Test
    fun previewViewIsNotClickable() {
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.CAMERA)
        onView(withId(R.id.cameraPreviewView)).check(matches(not(isClickable())))
    }

    @Test
    fun takePictureIsClickable() {
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.CAMERA)
        onView(withId(R.id.takePictureButton)).check(matches(isClickable()))
    }

    @Test
    fun selectingCategoryChangesButtonText() {
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.CAMERA)
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("category1")).perform(click())
        onView(withId(R.id.selectCategoryButton)).check(matches(withText("category1")))
    }

    @Test
    fun selectingCategoryChangesButtonColors() {
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.CAMERA)
        onView(withId(R.id.selectCategoryButton)).perform(click())
        onView(withText("category1")).perform(click())
        onView(withId(R.id.selectCategoryButton)).check(matches(hasTextColor(R.color.black)))
    }
}