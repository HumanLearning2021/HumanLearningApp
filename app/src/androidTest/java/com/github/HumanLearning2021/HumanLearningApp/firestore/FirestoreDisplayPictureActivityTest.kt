package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageActivity
import com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasAnyDrawable
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirestoreDownloadAndDisplayPictureTest {
    private val pic = runBlocking {
        val theInterface = FirestoreDatabaseService("demo")
        val cats = theInterface.getCategories()
        theInterface.getPicture(cats.first())!!
    }

    @get:Rule
    val testRule = ActivityScenarioRule<DisplayImageActivity>(
        Intent(
            ApplicationProvider.getApplicationContext(),
            DisplayImageActivity::class.java,
        ).putExtra("display_image_image", pic)
    )

    @Before
    fun setUp() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).apply {
            if (!isScreenOn)
                wakeUp()
        }
    }

    @FlakyTest(detail = "Glide appears not to play well with the CI")
    @Test
    fun appleIsDisplayedInView() {
        assertHasAnyDrawable(R.id.display_image_viewImage)
    }
}

