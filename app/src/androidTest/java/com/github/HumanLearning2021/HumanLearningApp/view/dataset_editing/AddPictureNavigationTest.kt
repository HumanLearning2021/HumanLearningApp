package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.HomeFragmentDirections
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AddPictureNavigationTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(
        Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        )
    )

    @BindValue
    @Demo2Database
    val dbManagement: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private val catSet = setOf<Category>(
        DummyCategory("cat1", "cat1"),
        DummyCategory("cat2", "cat2"),
        DummyCategory("cat3", "cat3"),
    )

    val categories = catSet.toTypedArray()

    val navController = TestNavHostController(
        ApplicationProvider.getApplicationContext())

    @Before
    fun setup() {
        hiltRule.inject()
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }




    @Test
    fun navigateToChoose() {
        navigateToAddPictureActivity()
        onView(withId(R.id.select_existing_picture)).perform(click())
        activityScenarioRule.scenario.onActivity {
            var currentFragmentContainer =
                it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            val currentFragment = currentFragmentContainer?.findNavController()?.currentDestination
            assert(currentFragment?.id == R.id.selectPictureFragment)
        }
    }

    @Test
    fun navigateToCamera() {
        navigateToAddPictureActivity()
        Espresso.onView(ViewMatchers.withId(R.id.use_camera))
            .perform(ViewActions.click())

        activityScenarioRule.scenario.onActivity {
            var currentFragmentContainer =
                it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            val currentFragment = currentFragmentContainer?.findNavController()?.currentDestination
            assert(currentFragment?.id == R.id.takePictureFragment)
        }
    }


    private fun launchFragment(args:Bundle) {
        launchFragmentInHiltContainer<DisplayDatasetFragment>(args) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    private fun navigateToAddPictureActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.goToDatasetsOverviewButton))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(ViewMatchers.withText(R.string.add_new_picture)).perform(click())

    }


}
