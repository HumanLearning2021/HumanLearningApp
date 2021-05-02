package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.Keep
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.BundleMatchers
import androidx.test.espresso.intent.matcher.BundleMatchers.hasEntry
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.hasName
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import kotlinx.parcelize.Parcelize
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SelectPictureActivityTest {
    private val catSet = setOf<Category>(
        DummyCategory("cat1", "cat1"),
        DummyCategory("cat2", "cat2"),
        DummyCategory("cat3", "cat3"),
    )

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @get:Rule
    val testRule = ActivityScenarioRule<SelectPictureActivity>(
        Intent(
            ApplicationProvider.getApplicationContext(), SelectPictureActivity::class.java
        ).putExtra("categories", ArrayList(catSet))
    )

    @Test
    fun correctLayoutIsDisplayAfterCreation() {
        BaristaVisibilityAssertions.assertDisplayed(R.id.choosePictureButton)
        BaristaVisibilityAssertions.assertDisplayed(R.id.selectCategoryButton2)
        BaristaVisibilityAssertions.assertDisplayed(R.id.saveButton3)
    }

    @Test
    fun categoriesCorrectlySetAfterCreation() {
        onView(withId(R.id.selectCategoryButton2)).perform(click())
        onView(withText("cat1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText("cat2"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText("cat3"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun intentSentOnSave() {
        val imageUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.knife)
        onView(withId(R.id.selectCategoryButton2)).perform(click())
        onView(withText("cat1")).perform(click())
        intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(
            Intent().run {
                data = imageUri
                Instrumentation.ActivityResult(SelectPictureActivity.RC_OPEN_PICTURE, this)
            })
        onView(withId(R.id.choosePictureButton)).perform(click())
        onView(withId(R.id.saveButton3)).perform(click())
        val result = testRule.scenario.result
        assertThat(result.resultCode, equalTo(Activity.RESULT_OK))
        assertThat(result.resultData, hasExtraWithKey("result"))
    }
}