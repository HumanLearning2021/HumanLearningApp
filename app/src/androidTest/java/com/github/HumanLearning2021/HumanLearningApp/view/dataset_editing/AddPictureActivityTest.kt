package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.TestUtils
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@UninstallModules(DatabaseManagementModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AddPictureActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @Demo2Database
    val dbManagement: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private val datasetId: String = TestUtils.getFirstDataset(dbManagement).id as String

    private val catSet = setOf<Category>(
        DummyCategory("cat1", "cat1"),
        DummyCategory("cat2", "cat2"),
        DummyCategory("cat3", "cat3"),
    )

    private val navController: NavController = Mockito.mock(NavController::class.java)


    @Before
    fun setup() {
        hiltRule.inject()
        launchFragment()
    }

    @Test
    fun correctLayoutIsDisplayAfterCreation() {
        BaristaVisibilityAssertions.assertDisplayed(R.id.select_existing_picture)
        BaristaVisibilityAssertions.assertDisplayed(R.id.use_camera)
    }

    @Test
    fun intentSentToChoose() {
        Espresso.onView(ViewMatchers.withId(R.id.select_existing_picture))
            .perform(ViewActions.click())

        verify(navController).navigate(AddPictureFragmentDirections.actionAddPictureFragmentToSelectPictureFragment(catSet.toTypedArray(), datasetId))
    }

    @Test
    fun intentSentToCamera() {
        Espresso.onView(ViewMatchers.withId(R.id.use_camera))
            .perform(ViewActions.click())

        verify(navController).navigate(AddPictureFragmentDirections.actionAddPictureFragmentToTakePictureFragment(catSet.toTypedArray(), datasetId))
    }

    /*
    This can only be tested from the source fragments under the current implementation

    @Test
    fun receiveIntentFromChoose() {
        val imageUri =
            Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.knife)
        Intents.intending(hasComponent(SelectPictureActivity::class.qualifiedName)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                Intent().putExtra(
                    "result",
                    bundleOf("category" to DummyCategory("cat1", "cat1"), "image" to imageUri)
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.select_existing_picture))
            .perform(ViewActions.click())
        val result = testRule.scenario.result
        MatcherAssert.assertThat(result.resultCode, Matchers.equalTo(Activity.RESULT_OK))
        MatcherAssert.assertThat(result.resultData, IntentMatchers.hasExtraWithKey("result"))
    }

    @Test
    fun receiveIntentFromCamera() {
        val imageUri =
            Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/" + R.drawable.knife)
        Intents.intending(hasComponent(TakePictureActivity::class.qualifiedName)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                Intent().putExtra(
                    "result",
                    bundleOf("category" to DummyCategory("cat1", "cat1"), "image" to imageUri)
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.use_camera))
            .perform(ViewActions.click())
        val result = testRule.scenario.result
        MatcherAssert.assertThat(result.resultCode, Matchers.equalTo(Activity.RESULT_OK))
        MatcherAssert.assertThat(result.resultData, IntentMatchers.hasExtraWithKey("result"))
    }

     */



    private fun launchFragment() {
        val args = bundleOf("categories" to catSet.toTypedArray(), "datasetId" to datasetId)
        launchFragmentInHiltContainer<AddPictureFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}