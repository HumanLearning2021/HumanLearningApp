package com.github.HumanLearning2021.HumanLearningApp.view

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GoogleSignInWidgetTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test_success() {
        launchFragmentInHiltContainer<GoogleSignInWidget> {
            onActivityResult(GoogleSignInWidget.RC_SIGN_IN, 0, null)
        }
    }
}