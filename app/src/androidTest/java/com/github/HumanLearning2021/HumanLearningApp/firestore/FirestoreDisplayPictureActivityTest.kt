package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Intent
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.hilt.DemoDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FirestoreCategorizedPictureTest {
    @Inject
    @DemoDatabase
    lateinit var db: DatabaseService
    lateinit var pic: CategorizedPicture

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    @get:Rule
    val testRule = IntentsTestRule(MainActivity::class.java, false, false)

    @Before
    fun setUp() {
        hiltRule.inject()  // to get db set up
        pic = runBlocking {
            val cats = db.getCategories()
            db.getPicture(cats.first())!!
        }
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).apply {
            if (!isScreenOn)
                wakeUp()
        }
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        testRule.launchActivity(intent)
    }

    @FlakyTest(detail = "Glide appears not to play well with the CI")
    @Test
    fun displayOnWorksAsExpected() {
        val imageView = ImageView(ApplicationProvider.getApplicationContext())

        testRule.activity.run {
            lifecycleScope.launch {
                setContentView(imageView)
                pic.displayOn(this@run, imageView)
                delay(1000)
                assertThat(imageView.drawable, notNullValue())
            }
        }
    }
}

