package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationPresenter(private val authUI: AuthUI, private val db: DatabaseService) {
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
    suspend fun onSuccessfulLogin() {
        Firebase.auth.currentUser?.let {
            _currentUser = db.updateUser(it)
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