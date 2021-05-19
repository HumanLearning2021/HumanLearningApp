package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.launch
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

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
        testRule.launchActivity(
            Intent(
                ApplicationProvider.getApplicationContext(),
                MainActivity::class.java
            )
        )
    }

    @Test
    fun displayOnWorksAsExpected() {
        val pictureUri =
            Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.fork)
        val dummyCategory = DummyCategory("Fork", "Fork")
        val dummyCategorizedPicture = DummyCategorizedPicture("some id", dummyCategory, pictureUri)
        val drawable = AppCompatResources.getDrawable(
            ApplicationProvider.getApplicationContext(),
            R.drawable.fork
        )
        val imageView = ImageView(ApplicationProvider.getApplicationContext())

        testRule.activity.run {
            lifecycleScope.launch {
                setContentView(imageView)
                dummyCategorizedPicture.displayOn(this@run, imageView)

            }
            waitFor(500)
            //for a lack of a better way to compare drawables
            assert(
                imageView.drawable.toBitmap().getPixel(5, 5) == drawable!!.toBitmap().getPixel(5, 5)
            )
        }
    }

    // PNG format signature per https://tools.ietf.org/html/rfc2083#section-3.1
    private fun pngHeader(): Array<Byte> = arrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10)

    @Test
    fun copyToWorksAsExpected() {
        val pictureUri =
            Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.fork)
        val dummyCategory = DummyCategory("Fork", "Fork")
        val dummyCategorizedPicture = DummyCategorizedPicture("some id", dummyCategory, pictureUri)
        val context: Context = ApplicationProvider.getApplicationContext()
        val tmpFile = File.createTempFile("myImage", ".png")
        try {
            dummyCategorizedPicture.copyTo(context, tmpFile)
            assertThat(
                tmpFile.inputStream().use {
                    val buf = ByteArray(8)
                    it.read(buf)
                    buf
                },
                equalTo(pngHeader())
            )
        } finally {
            tmpFile.delete()
        }
    }
}