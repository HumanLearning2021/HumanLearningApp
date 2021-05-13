package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DummyDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton  // need to persist currentUser across activities
class AuthenticationPresenter @Inject constructor(
    private val authUI: AuthUI,
    @DummyDatabase
    private val db: DatabaseService,
) {
    /**
     * Create an Intent that can be used to perform authentication via startActivityForResult
     */
    fun intentForStartActivityForResult(): Intent {
        val providers = listOf(AuthUI.IdpConfig.GoogleBuilder().build())
        return authUI.createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
    }

    /**
     * Callback to be executed by the view to let the presenter register a successful login
     */
    suspend fun onSuccessfulLogin(isAdmin :Boolean) {
        Firebase.auth.currentUser?.let {
            _currentUser = db.updateUser(it)
            _currentUser = db.setAdminAccess(it,isAdmin)

        }
    }

    private var _currentUser: User? = null

    private fun checkStaleCurrentUser() {
        if (Firebase.auth.currentUser?.uid != _currentUser?.uid) {
            _currentUser = null
        }
    }

    /**
     * Return information about the currently signed-in user
     */
    val currentUser: User?
        get() {
            checkStaleCurrentUser()
            return _currentUser
        }
}