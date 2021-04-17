package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import com.github.HumanLearning2021.HumanLearningApp.DataCreationActivity
import com.github.HumanLearning2021.HumanLearningApp.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.google.common.base.Predicates.instanceOf
import com.google.firebase.firestore.util.Assert.fail
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import java.lang.IllegalArgumentException

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DummyCategorizedPictureTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val testRule = IntentsTestRule(MainActivity::class.java, false, false)

    @Before
    fun setUp() {
        hiltRule.inject()
        testRule.launchActivity(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))
    }

    @Test
    fun displayOnWorksAsExpected() {
        val pictureUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork)
        val dummyCategory = DummyCategory("Fork", "Fork")
        val dummyCategorizedPicture = DummyCategorizedPicture("some id", dummyCategory, pictureUri)
        val drawable = AppCompatResources.getDrawable(ApplicationProvider.getApplicationContext(), R.drawable.fork)
        val imageView = ImageView(ApplicationProvider.getApplicationContext())

        testRule.activity.run {
            lifecycleScope.launch {
            setContentView(imageView)
            dummyCategorizedPicture.displayOn(this@run, imageView)

            //for a lack of a better way to compare drawables
            assert(imageView.drawable.toBitmap().getPixel(5, 5) == drawable!!.toBitmap().getPixel(5, 5))
        }
        }
    }
}