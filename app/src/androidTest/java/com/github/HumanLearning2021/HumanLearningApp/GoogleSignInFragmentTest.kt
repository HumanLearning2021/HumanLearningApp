package com.github.HumanLearning2021.HumanLearningApp

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.view.GoogleSignInFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoogleSignInFragmentTest {
    lateinit var fragmentScenario: FragmentScenario<GoogleSignInFragment>

    @Before
    fun startFragment() {
        fragmentScenario = FragmentScenario.launch(GoogleSignInFragment::class.java)
    }

    @Test
    fun test_success() {
        fragmentScenario.onFragment {
            it.onActivityResult(GoogleSignInFragment.RC_SIGN_IN, 0, null)
        }
    }
}