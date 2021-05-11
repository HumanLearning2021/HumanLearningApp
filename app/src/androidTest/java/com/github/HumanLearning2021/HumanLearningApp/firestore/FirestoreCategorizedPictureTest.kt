package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.HumanLearning2021.HumanLearningApp.TestUtils.waitFor
import com.github.HumanLearning2021.HumanLearningApp.hilt.*
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.room.RoomOfflineDatabase
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
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

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FirestoreCategorizedPictureTest {

    @Inject
    @Demo2Database
    lateinit var demo2DbService: DatabaseService

    @BindValue
    @Demo2Database
    lateinit var demo2DbMgt: DatabaseManagement

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    @RoomDatabase
    lateinit var room: RoomOfflineDatabase

    @Inject
    @DemoDatabase
    lateinit var db: DatabaseService

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val testRule = IntentsTestRule(MainActivity::class.java, false, false)

    @Before
    fun setUp() {
        hiltRule.inject()  // to get db set up
        demo2DbMgt = DatabaseManagementModule.provideDemo2Service(demo2DbService, context, room)
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

        testRule.activity.run {
            lifecycleScope.launch {
                setContentView(imageView)
                pic.displayOn(this@run, imageView)
            }
        }
        waitFor(1000)
        assertThat(imageView.drawable, notNullValue())
    }
}

