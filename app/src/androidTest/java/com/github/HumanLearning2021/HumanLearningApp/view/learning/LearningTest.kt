package com.github.HumanLearning2021.HumanLearningApp.view.learning

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DefaultDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito


@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LearningTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbManagement: DatabaseManagement = DefaultDatabaseManagement(DummyDatabaseService())

    private val datasetId = TestUtils.getFirstDataset(dbManagement).id

    private val navController: NavController = Mockito.mock(NavController::class.java)

    val NUMBER_OF_CATEGORIES = 3
    val CATEGORY_VIEW_CLASS_NAME = "android.widget.ImageView"
    val NUMBER_OF_DRAG_STEPS = 10

    lateinit var mDevice: UiDevice

    lateinit var learningFragment: LearningFragment

    @Before
    fun setup() {
        mDevice = UiDevice.getInstance(getInstrumentation())
        hiltRule.inject()
        launchFragment()
    }

    @Test
    fun allImageViewsAreDisplayed() {
        assertDisplayed(R.id.learning_to_sort)
        assertDisplayed(R.id.learning_cat_0)
        assertDisplayed(R.id.learning_cat_1)
        assertDisplayed(R.id.learning_cat_2)
    }

    /** TODO add test to verify that the LearningFragment can support datasets with
     * 1, 2, 3 or more categories
     */

    /**
     * This test verifies that when dragging the picture to sort on each of the targets a certain
     * number of times (eg. 10), the category changed at least once. This test DOES NOT use
     * contentDescription to do verifications, but it is less precise than the other tests because
     * it cannot select the correct (or incorrect) target, so it has to try them all in succession.
     */
    @Test
    fun atLeastOneCategoryChange() {
        val imToSort = getImageToSort()
        val targetImages = getTargetImages()
        val nbAttempts = 10
        var foundChange = false
        for (i in 1..nbAttempts) {
            // try all targets in succession
            for (target in targetImages) {
                val catBefore = getCurrentCategory()
                imToSort.dragTo(target, NUMBER_OF_DRAG_STEPS)
                val catAfter = getCurrentCategory()
                if (catBefore != catAfter) {
                    foundChange = true
                    break
                }
            }
            if (foundChange) break
        }
        assert(foundChange) {
            "There was no change of category even after dragging the " +
                    "image to sort $nbAttempts times on each target."
        }
    }

    private fun getCurrentCategory() =
        learningFragment.learningPresenter.learningModel.getCurrentCategory()

    private fun getTargetImages(): List<UiObject> =
        (0 until NUMBER_OF_CATEGORIES).map {
            getImageInstance(it)
        }


    /**
     * WARNING: this test relies on the fact that the contentDescription of the ImageViews
     * on the learningFragment corresponds to the name of the category displayed on it.
     * I (Niels) haven't found another way to select the correct UiObject as drag target, using
     * UiAutomation.
     */
    @Test
    fun dragImageOnCorrectCategory() {
        val imToSort = getImageToSort()
        val startDescr = imToSort.contentDescription
        val NUMBER_OF_ATTEMPTS = 50
        var foundImageChange = false
        // tries drag and drop NUMBER_OF_ATTEMPTS times until there is a change in the image to sort
        // (change in category)
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

    /**
     * WARNING: this test relies on the fact that the contentDescription of the ImageViews
     * on the learningFragment corresponds to the name of the category displayed on it.
     * I (Niels) haven't found another way to select the correct UiObject as drag target, using
     * UiAutomation.
     */
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
     *
     * WARNING: this test relies on the fact that the contentDescription of the ImageViews
     * on the learningFragment corresponds to the name of the category displayed on it.
     * I (Niels) haven't found another way to select the correct UiObject as drag target, using
     * UiAutomation.
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

    private fun launchFragment() {
        val args = bundleOf("datasetId" to datasetId, "learningMode" to LearningMode.PRESENTATION)
        launchFragmentInHiltContainer<LearningFragment>(args) {
            // intercepts the LearningFragment for use during tests
            // I am quite surprised that this works, but I am very happy that it does
            learningFragment = this
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}
