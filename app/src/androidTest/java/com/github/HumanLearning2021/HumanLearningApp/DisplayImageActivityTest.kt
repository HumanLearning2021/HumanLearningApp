package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import android.os.Parcelable
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.presenter.DummyUIPresenter
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayImageActivity
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.Serializable

@RunWith(AndroidJUnit4::class)
class DisplayImageActivityTest {
    @get:Rule
    var activityRuleIntent = IntentsTestRule(DisplayImageActivity::class.java, false, false)

    val categoryImagesList = ArrayList<CategorizedPicture>()
    val dummydsService = DummyDatabaseService()
    val dummyPresenter = DummyUIPresenter(DummyDatabaseService())

    @Before
    fun setUp() {
        runBlocking {
            val categories = dummydsService.getCategories().toList()
            categoryImagesList.add(dummyPresenter.getPicture(categories[0].name)!!)
            val test = dummydsService.getPicture(dummydsService.getCategory("Fork")!!)
        }
        val intent = Intent()
        intent.putExtra("display_image_image", (categoryImagesList[0]) as Parcelable)
        activityRuleIntent.launchActivity(intent)
    }


    @Test
    fun PictureAndCategoryAreDisplayed() {
        onView(withId(R.id.display_image_viewImage))
            .check(matches(isDisplayed()))
        onView(withId(R.id.display_image_viewCategory))
            .check(matches(isDisplayed()))
        onView(withId(R.id.display_image_delete_button)).check(matches(isDisplayed()))
    }

}