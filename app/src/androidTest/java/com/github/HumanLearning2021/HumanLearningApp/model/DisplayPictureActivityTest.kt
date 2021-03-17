package com.github.HumanLearning2021.HumanLearningApp.model

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.github.HumanLearning2021.HumanLearningApp.R
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule
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
    fun viewForkIsDisplayed(){
        assertDisplayed(R.id.DisplayFork_imageView)
    }

    @Test
    fun viewSpoonIsDisplayed(){
        assertDisplayed(R.id.DisplaySpoon_imageView)
    }

    @Test
    fun viewKnifeIsDisplayed(){
        assertDisplayed(R.id.DisplayKnife_imageView)
    }


    @Test
    fun forkIsDisplayedInView(){
        assertHasDrawable(R.id.DisplayFork_imageView, R.drawable.fork)
    }

    @Test
    fun spoonIsDisplayedInView(){
        assertHasDrawable(R.id.DisplaySpoon_imageView, R.drawable.spoon)
    }

    @Test
    fun knifeIsDisplayedInView(){
        assertHasDrawable(R.id.DisplayKnife_imageView, R.drawable.knife)
    }


}
