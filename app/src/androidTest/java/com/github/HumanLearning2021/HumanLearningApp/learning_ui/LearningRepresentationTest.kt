package com.github.HumanLearning2021.HumanLearningApp.learning_ui
import android.content.Intent
import android.widget.ImageView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.view.LearningActivity
import com.github.HumanLearning2021.HumanLearningApp.view.LearningMode
import com.github.HumanLearning2021.HumanLearningApp.view.LearningSettingsActivity
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LearningRepresentationTest {

    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<LearningActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            LearningActivity::class.java
        ).putExtra(LearningSettingsActivity.EXTRA_LEARNING_MODE, LearningMode.REPRESENTATION))



    val NUMBER_OF_CATEGORIES = 3
    val CATEGORY_VIEW_CLASS_NAME = "android.widget.ImageView"
    val NUMBER_OF_DRAG_STEPS = 10

    lateinit var mDevice: UiDevice
    @Before
    fun init() {
        mDevice = UiDevice.getInstance(getInstrumentation())
    }

    @Test
    fun imageToClassifyIsNotRepresentativePicture(){
        // TODO: couldn't yet find a way to test whether the picture held by the view was part of one set and not another
    }

    @Test
    fun targetImagesAreRepresentativePictures(){
        // TODO: couldn't yet find a way to test whether the picture held by the view was part of one set and not another
    }

    @Test
    fun allImageViewsAreDisplayed(){
        viewWithIdisDisplayed(R.id.learning_im_to_sort)
        viewWithIdisDisplayed(R.id.learning_cat_0)
        viewWithIdisDisplayed(R.id.learning_cat_1)
        viewWithIdisDisplayed(R.id.learning_cat_2)
    }

    private fun viewWithIdisDisplayed(id: Int) = onView(withId(id)).check(matches(isDisplayed()))

}

