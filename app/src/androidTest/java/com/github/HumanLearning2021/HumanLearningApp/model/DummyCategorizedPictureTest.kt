package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import kotlinx.parcelize.Parcelize
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import java.lang.IllegalArgumentException

@RunWith(AndroidJUnit4::class)
class DummyCategorizedPictureTest {

    @get:Rule
    val activityScenario = ActivityScenarioRule<AppCompatActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))

    @get:Rule
    val exception = ExpectedException.none()

    @Parcelize
    class testCat(
        override val id: String,
        override val name: String
    ) : Category

    @Test
    fun displayOnWorksAsExpected() {
        val pictureUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork)
        val dummyCategory = DummyCategory("Fork", "Fork")
        val dummyCategorizedPicture = DummyCategorizedPicture(dummyCategory, pictureUri)
        val drawable = AppCompatResources.getDrawable(ApplicationProvider.getApplicationContext(), R.drawable.fork)
        val imageView = ImageView(ApplicationProvider.getApplicationContext())

        activityScenario.scenario.onActivity {activity ->
            activity.run {
                setContentView(imageView)
                dummyCategorizedPicture.displayOn(activity, imageView)
            }
        }

        //for a lack of a better way to compare drawables
        assert(imageView.drawable.toBitmap().getPixel(5, 5) == drawable!!.toBitmap().getPixel(5, 5))
    }



    @Test
    fun displayOnThrowsExpectedException() {
        val pictureUri = Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork)
        val testCategory = testCat("Fork","Fork")
        val dummyCategorizedPicture = DummyCategorizedPicture(testCategory, pictureUri)
        activityScenario.scenario.onActivity { activity ->
            try {
                dummyCategorizedPicture.displayOn(activity, ImageView(ApplicationProvider.getApplicationContext()))
            } catch (e: IllegalArgumentException) {
                assert(true)
            }
        }
    }
}