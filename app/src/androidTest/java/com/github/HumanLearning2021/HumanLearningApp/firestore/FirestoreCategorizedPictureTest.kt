package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Intent
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.HumanLearning2021.HumanLearningApp.hilt.TestDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultImageDisplayer
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assume.assumeThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FirestoreCategorizedPictureTest {
    @Inject
    @TestDatabase
    lateinit var db: DatabaseService

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val testRule = IntentsTestRule(MainActivity::class.java, false, false)

    @Before
    fun setUp() {
        hiltRule.inject()  // to get db set up
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).apply {
            if (!isScreenOn)
                wakeUp()
        }
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        testRule.launchActivity(intent)
    }

    @Test
    fun displayOnWorksAsExpected() {
        val pic = runBlocking {
            val cats = db.getCategories()
            db.getPicture(cats.first())
        }
        assumeThat("no picture to test display", pic, notNullValue())
        pic!!

        val imageView = ImageView(ApplicationProvider.getApplicationContext())
        assumeThat(imageView.drawable, nullValue())

        val job = testRule.activity.run {
            lifecycleScope.launch {
                setContentView(imageView)
                with(DefaultImageDisplayer(this@run)) {
                    pic.displayOn(imageView)
                }
            }
        }
        runBlocking { job.join() }
        assertThat(imageView.drawable, notNullValue())
    }
}

