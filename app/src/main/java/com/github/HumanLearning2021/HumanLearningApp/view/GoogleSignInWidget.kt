package com.github.HumanLearning2021.HumanLearningApp.view

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.firebase.ui.auth.IdpResponse
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GoogleSignInWidget : Fragment() {
    @Inject
    lateinit var presenter: AuthenticationPresenter

    var isAdmin = false
    var prefs: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_google_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = activity?.getSharedPreferences("LOGIN", MODE_PRIVATE)
        editor = prefs!!.edit()

        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            onLoginButtonPress()
        }

        view.findViewById<Button>(R.id.checkBox).setOnClickListener {
            isAdmin = view.findViewById<CheckBox>(R.id.checkBox).isChecked
        }
        view.findViewById<Button>(R.id.singOutButton).setOnClickListener {
            onSignOutPress()

        }

        updateUi()
    }

    private fun onSignOutPress() {
        lifecycleScope.launch {
            presenter.signOut()
        }
        editor?.putString("name", "")
        editor?.putString("email", "")
        editor?.putBoolean("hasLogin", false)
        editor?.putBoolean("isAdmin", false)
        editor?.apply()

        updateUi()
    }

    private fun onLoginButtonPress() {
        /** This method is deprecated. However, the [official documentation][1] recommends using it
         * for this purpose as of 2021-05-12. We do not currently know how to avoid this.
         *
         * [1]:
         * https://firebase.google.com/docs/auth/android/google-signin#authenticate_with_firebase
         */
        @Suppress("DEPRECATION")
        startActivityForResult(
            presenter.intentForStartActivityForResult(),
            RC_SIGN_IN
        )
    }

    private fun handleSignIn() {
        val user = presenter.currentUser
        if (user != null) {
            editor?.putString("name", user?.displayName)
            editor?.putString("email", user?.email)
            editor?.putBoolean("hasLogin", true)
            editor?.putBoolean("isAdmin", isAdmin)
            editor?.apply()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                val response = IdpResponse.fromResultIntent(data)

                when (resultCode) {
                    Activity.RESULT_OK -> {
                        lifecycleScope.launch {
                            presenter.onSuccessfulLogin(isAdmin)
                            handleSignIn()
                            updateUi()
                        }
                    }
                    else -> {
                        if (response == null) {
                            // cancelled by user
                            updateUi()
                        } else {
                            Log.e(
                                "GoogleSignInWidget",
                                "Error during authentication",
                                response.error
                            )
                        }
                    }
                }
            }
        }
    }


    private fun updateUi() {
        if (prefs!!.getBoolean("hasLogin", false)) {
            view?.findViewById<TextView>(R.id.loginStatus)?.text =
                prefs?.getString("name", "Not logged in!")
            isAdmin = prefs?.getBoolean("isAdmin", false)!!
            view?.findViewById<Button>(R.id.singOutButton)?.visibility = View.VISIBLE
            view?.findViewById<Button>(R.id.checkBox)?.visibility = View.GONE
            view?.findViewById<Button>(R.id.loginButton)?.visibility = View.GONE
        } else {
            val user = presenter.currentUser
            view?.findViewById<TextView>(R.id.loginStatus)?.text =
                user?.let {
                    getString(
                        R.string.SignInFragment_loginStatusSuccessMessage,
                        user.displayName
                    )
                } ?: "Not logged in!"
            if (user == null) {
                view?.findViewById<Button>(R.id.singOutButton)?.visibility = View.GONE
                view?.findViewById<Button>(R.id.checkBox)?.visibility = View.VISIBLE
                view?.findViewById<Button>(R.id.loginButton)?.visibility = View.VISIBLE
            } else {
                view?.findViewById<Button>(R.id.singOutButton)?.visibility = View.VISIBLE
                view?.findViewById<Button>(R.id.checkBox)?.visibility = View.GONE
                view?.findViewById<Button>(R.id.loginButton)?.visibility = View.GONE            }
        }
    }

    companion object {
        val RC_SIGN_IN: Int = "firebase auth".hashCode()
        var isAdmin = false
    }
}