package com.github.HumanLearning2021.HumanLearningApp

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.fragments.DatasetListFragment
import com.github.HumanLearning2021.HumanLearningApp.view.fragments.DatasetListRecyclerViewAdapter
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DatasetListFragmentTest {
    private lateinit var dummyDatasets: Set<Dataset>
    private lateinit var scenario: FragmentScenario<DatasetListFragment>

    @Before
    fun setUp() {
        runBlocking {
            dummyDatasets = DummyDatabaseManagement.staticDummyDatabaseManagement.getDatasets()
        }

        scenario = launchFragmentInContainer()

        val delayBeforeTestStart: Long = 1000
        TestUtils.waitFor(delayBeforeTestStart)
    }

    @Test
    fun listItemInFragmentAreClickable() {
        scenario.withFragment {
            this.selectedDataset.observe(this) {
                // tests that the clicked dataset is in the dummy datasets
                assert(dummyDatasets.find { ds -> ds == it } != null
                ) { "clicked dataset $it was not in $dummyDatasets" }
            }
        }
        onView(withId(R.id.DatasetList_list))
            .perform(
                actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )
    }

    @Test
    fun fragmentHasChildrenViews() {
        onView(withId(R.id.DatasetList_list)).check(
            matches(
                ViewMatchers.hasChildCount(dummyDatasets!!.size)
            )
        )
    }

    // TODO test that category representative are correctly displayed on each item
}