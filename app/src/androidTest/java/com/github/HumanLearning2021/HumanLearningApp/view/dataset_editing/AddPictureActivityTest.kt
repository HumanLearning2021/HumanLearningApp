package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddPictureActivityTest {
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
    val testRule = ActivityScenarioRule<AddPictureActivity>(
        Intent(
            ApplicationProvider.getApplicationContext(), AddPictureActivity::class.java
        ).putExtra("categories", ArrayList(catSet))
    )

    @Test
    fun correctLayoutIsDisplayAfterCreation() {
        BaristaVisibilityAssertions.assertDisplayed(R.id.select_existing_picture)
        BaristaVisibilityAssertions.assertDisplayed(R.id.use_camera)
    }

    @Test
    fun intentSentToChoose() {
        Espresso.onView(ViewMatchers.withId(R.id.select_existing_picture))
            .perform(ViewActions.click())
        Intents.intended(hasComponent(SelectPictureActivity::class.qualifiedName))
    }

    @Test
    fun intentSentToCamera() {
        Espresso.onView(ViewMatchers.withId(R.id.use_camera))
            .perform(ViewActions.click())
        Intents.intended(hasComponent(TakePictureActivity::class.qualifiedName))
    }

    @Test
    fun receiveIntentFromChoose() {
        val imageUri =
            Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.knife)
        Intents.intending(hasComponent(SelectPictureActivity::class.qualifiedName)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                Intent().putExtra(
                    "result",
                    bundleOf("category" to DummyCategory("cat1", "cat1"), "image" to imageUri)
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.select_existing_picture))
            .perform(ViewActions.click())
        val result = testRule.scenario.result
        MatcherAssert.assertThat(result.resultCode, Matchers.equalTo(Activity.RESULT_OK))
        MatcherAssert.assertThat(result.resultData, IntentMatchers.hasExtraWithKey("result"))
    }

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
}