package com.github.HumanLearning2021.HumanLearningApp.view

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import com.github.HumanLearning2021.HumanLearningApp.model.User
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Fragment where the user can sign in with his Google account
 */
@AndroidEntryPoint
class GoogleSignInFragment : Fragment() {

    @Inject
    lateinit var presenter: AuthenticationPresenter

    var isAdmin = false
    lateinit var prefs: SharedPreferences
    lateinit private var editor: SharedPreferences.Editor


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_google_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = activity?.getSharedPreferences("LOGIN", MODE_PRIVATE)!!
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

    fun onSignOutPress() {
        lifecycleScope.launch {
            presenter.signOut()
        }
        editor.putString("name", "")
        editor.putString("email", "")
        editor.putString("uid", "")
        editor.putBoolean("hasLogin", false)
        editor.putBoolean("isAdmin", false)
        editor.apply()
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

    fun handleSignIn() {
        presenter.currentUser?.let { user ->
            editor.putString("name", user.displayName)
            editor.putString("email", user.email)
            editor.putString("uid", user.uid)
            editor.putBoolean("hasLogin", true)
            editor.putBoolean("isAdmin", isAdmin)
            editor.apply()
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

    fun setSignInUi() {
        view?.findViewById<Button>(R.id.singOutButton)?.visibility = View.GONE
        view?.findViewById<Button>(R.id.checkBox)?.visibility = View.VISIBLE
        view?.findViewById<Button>(R.id.loginButton)?.visibility = View.VISIBLE
    }

    fun setSignOutUi() {
        view?.findViewById<Button>(R.id.singOutButton)?.visibility = View.VISIBLE
        view?.findViewById<Button>(R.id.checkBox)?.visibility = View.GONE
        view?.findViewById<Button>(R.id.loginButton)?.visibility = View.GONE
    }

    fun updateUi() {
        val savedUser: User?
        if (prefs.getBoolean("hasLogin", false)) {
            view?.findViewById<TextView>(R.id.loginStatus)?.text =
                prefs.getString("name", "Not logged in!")
            savedUser = User(
                prefs.getString("name", ""),
                prefs.getString("email", ""),
                prefs.getString("uid", "")!!,
                User.Type.FIREBASE,
                prefs.getBoolean("isAdmin", false)
            )
            setSignOutUi()

        } else {
            val loggedUser = presenter.currentUser
            view?.findViewById<TextView>(R.id.loginStatus)?.text =
                loggedUser?.let {
                    getString(
                        R.string.SignInFragment_loginStatusSuccessMessage,
                        loggedUser.displayName
                    )
                } ?: "Not logged in!"
            if (loggedUser == null) {
                setSignInUi()
            } else {
                setSignOutUi()
            }
        }
    }

    companion object {
        val RC_SIGN_IN: Int = "firebase auth".hashCode()
        var isAdmin = false
    }
}