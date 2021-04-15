package com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import com.github.HumanLearning2021.HumanLearningApp.view.GoogleSignInFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GoogleSignInFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    @Test
    fun test_success() {
        launchFragmentInHiltContainer<GoogleSignInFragment> {
            onActivityResult(GoogleSignInFragment.RC_SIGN_IN, 0, null)
        }
    }
}