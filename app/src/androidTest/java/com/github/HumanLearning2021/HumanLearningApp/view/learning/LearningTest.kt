package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// TODO: fix
@FlakyTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LearningTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityScenarioRule: ActivityScenarioRule<LearningActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            LearningActivity::class.java
        )
            .putExtra(LearningSettingsActivity.EXTRA_LEARNING_MODE, LearningMode.PRESENTATION)
            .putExtra(
                LearningDatasetSelectionActivity.EXTRA_SELECTED_DATASET,
                getDS()
            )
    )

    private fun getDS() = runBlocking {
        DummyDatabaseManagement(DummyDatabaseService()).getDatasets().first()
    }

    val NUMBER_OF_CATEGORIES = 3
    val CATEGORY_VIEW_CLASS_NAME = "android.widget.ImageView"
    val NUMBER_OF_DRAG_STEPS = 10

    lateinit var mDevice: UiDevice

    @Before
    fun init() {
        mDevice = UiDevice.getInstance(getInstrumentation())
    }

    @Test
    fun allImageViewsAreDisplayed() {
        assertDisplayed(R.id.learning_im_to_sort)
        assertDisplayed(R.id.learning_cat_0)
        assertDisplayed(R.id.learning_cat_1)
        assertDisplayed(R.id.learning_cat_2)
    }

    @Test
    fun dragImageOnCorrectCategory() {
        val imToSort = getImageToSort()
        val startDescr = imToSort.contentDescription
        val NUMBER_OF_ATTEMPTS = 50
        var foundImageChange = false
        // tries drag and drop NUMBER_OF_ATTEMPTS times until there is a change in the image to sort
        // (change in description)
        // prob to have the same color NUMBER_OF_ATTEMPTS (eg 100) times in a row with a working UI is
        // P = (1/NUMBER_OF_CATEGORIES)^NUMBER_OF_ATTEMPTS (eg (1/3)^100, aka very small)
        for (i in 1..NUMBER_OF_ATTEMPTS) {
            if (imToSort.contentDescription != startDescr) {
                foundImageChange = true
                break
            }
            val corresCat = getObjectByDescr(imToSort.contentDescription)
            imToSort.dragTo(corresCat, NUMBER_OF_DRAG_STEPS)
        }
        assertThat(foundImageChange, `is`(true))
    }

    @Test
    fun dragImageOnIncorrectCategory() {
        val imToSort = getImageToSort()
        val startDescr = imToSort.contentDescription
        for (i in 0 until NUMBER_OF_CATEGORIES) {
            val im = getImageInstance(i)
            if (im.contentDescription != imToSort.contentDescription) {
                imToSort.dragTo(im, NUMBER_OF_DRAG_STEPS)
                // if the image to sort has not changed, the incorrect category has not accepted
                // the image, which is what we want
                assertThat(imToSort.contentDescription, `is`(startDescr))
            }
        }
    }

    /**
     * This test verifies that nothing changes if the drop fails.
     * It also allows to test what happens when ACTION_DRAG_EXITED happens, because the drag should
     * pass over a category when going from bottom center (image to sort) to top left.
     * WARNING: this is not ideal, because 1) if a category happens to be in the top left corner
     * and it is the correct category, the test will fail.
     * And 2) if a category is not on the straight line between the image to sort and the top left,
     * ACTION_DRAG_EXITED will never get triggered and thus decrease branch coverage.
     * If anyone has a smarter idea to trigger the ACTION_DRAG_EXITED without the aforementioned
     * caveats, I'll be more than happy to hear it.
     */
    @Test
    fun dragWithUnsuccessfulDrop() {
        val imToSort = getImageToSort()
        val startDescr = imToSort.contentDescription
        // there shouldn't be a category on the topleft corner
        imToSort.dragTo(0, 0, NUMBER_OF_DRAG_STEPS)
        assertThat(startDescr, `is`(imToSort.contentDescription))
    }

    private fun getImageInstance(instance: Int) = mDevice.findObject(
        UiSelector().className(CATEGORY_VIEW_CLASS_NAME)
            .instance(instance)
    )

    /**
     * instance at index NUMBER_OF_CATEGORIES (eg. 3) is the image to sort
     */
    private fun getImageToSort() = getImageInstance(NUMBER_OF_CATEGORIES)

    private fun getObjectByDescr(descr: String): UiObject {
        return mDevice.findObject(UiSelector().description(descr))
    }

}

