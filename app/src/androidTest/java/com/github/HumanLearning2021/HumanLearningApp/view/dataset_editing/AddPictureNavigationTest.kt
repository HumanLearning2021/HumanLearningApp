package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.Manifest
import android.content.Intent
import androidx.navigation.fragment.findNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseNameModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListRecyclerViewAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.schibsted.spain.barista.interaction.PermissionGranter
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@UninstallModules(DatabaseNameModule::class)
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

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @BindValue
    @ProductionDatabaseName
    val dbName = "dummy"

    lateinit var dbMgt: DatabaseManagement

    @BindValue
    val authPresenter = AuthenticationPresenter(AuthUI.getInstance(), DummyDatabaseService())

    private val catSet = setOf(
        Category("cat1", "cat1"),
        Category("cat2", "cat2"),
        Category("cat3", "cat3"),
    )

    val categories = catSet.toTypedArray()

    val navController = TestNavHostController(
        ApplicationProvider.getApplicationContext()
    )

    @Before
    fun setup() {
        PermissionGranter.allowPermissionOneTime(Manifest.permission.CAMERA)
        hiltRule.inject()
        dbMgt = globalDatabaseManagement.accessDatabase(dbName)
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
        activityScenarioRule.scenario.close()
    }

    @Test
    fun navigateToChoose() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.button_start_learning)).perform(click())
            navigateToAddPictureActivity()
            onView(withId(R.id.button_select_existing_picture)).perform(click())
            assertCurrentFragmentIsCorrect(R.id.selectPictureFragment)
        }
    }

    @Test
    fun navigateToCamera() {
        runBlocking {
            Firebase.auth.signInAnonymously().await().user!!
            authPresenter.onSuccessfulLogin(true)
            onView(withId(R.id.button_start_learning)).perform(click())
            navigateToAddPictureActivity()
            onView(withId(R.id.use_camera))
                .perform(click())

            assertCurrentFragmentIsCorrect(R.id.takePictureFragment)
        }
    }


    private fun navigateToAddPictureActivity() {
        onView(withId(R.id.datasetsOverviewFragment)).perform(click())
        onView(withId(R.id.DatasetList_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<DatasetListRecyclerViewAdapter.ListItemViewHolder>(
                    0,
                    click()
                )
            )
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(ViewMatchers.withText(R.string.add_new_picture)).perform(click())
    }

    private fun assertCurrentFragmentIsCorrect(expected: Int) {
        activityScenarioRule.scenario.onActivity {
            val currentFragmentContainer =
                it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            val currentFragment = currentFragmentContainer?.findNavController()?.currentDestination
            assert(currentFragment?.id == expected)
        }
    }
}
