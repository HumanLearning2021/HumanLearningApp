package com.github.HumanLearning2021.HumanLearningApp.Model

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
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.github.HumanLearning2021.HumanLearningApp.Model.DisplayPictureActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
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
class DisplayPictureActivityTest {



    @get:Rule
    var testRule = ActivityTestRule(DisplayPictureActivity::class.java)

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
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.github.HumanLearning2021.HumanLearningApp", appContext.packageName)
    }

    @Test
    fun viewIsDisplayed(){
        assertDisplayed(R.id.imageView)
    }

    @Test
    fun forkIsDisplayedInView(){
        assertHasDrawable(R.id.imageView, R.drawable.spoon)
    }

}