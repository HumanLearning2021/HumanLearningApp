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
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.IdpResponse
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Fragment used to log in & out the user and set the access privileges.
 */
@AndroidEntryPoint
class GoogleSignInWidget : Fragment() {

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

        view.findViewById<Button>(R.id.button_login).setOnClickListener {
            onLoginButtonPress()
        }

        view.findViewById<Button>(R.id.checkBox).setOnClickListener {
            isAdmin = view.findViewById<CheckBox>(R.id.checkBox).isChecked
        }

        view.findViewById<Button>(R.id.button_sign_out).setOnClickListener {
            onSignOutPress()
        }

        updateUi()
    }

    /**
     * Handles the logging out of the user.
     *
     * Clears the informations of the current logged in user from the shared preferences
     * and resets the user access priviliges.
     */
    fun onSignOutPress() {
        lifecycleScope.launch {
            presenter.signOut()
        }
        editor?.putString("name", "")
        editor?.putString("email", "")
        editor?.putString("uid", "")
        editor?.putBoolean("hasLogin", false)
        editor?.putBoolean("isAdmin", false)
        editor?.apply()
        findNavController().navigate(GoogleSignInWidgetDirections.actionGoogleSignInWidgetToHomeFragment())
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

    /**
     * Handles the logging in of the user.
     *
     * puts the informations of the current logged in user in the shared preferences
     * to persist the login after closing the app
     */
    fun handleSignIn() {
        val user = presenter.currentUser
        if (user != null) {
            editor?.putString("name", user?.displayName)
            editor?.putString("email", user?.email)
            editor?.putString("uid", user?.uid)
            editor?.putBoolean("hasLogin", true)
            editor?.putBoolean("isAdmin", isAdmin)
            editor?.apply()
        }
    }

    /**
     * Handles the logging in of the user.
     *
     * puts the informations of the current logged in user in the shared preferences
     * to persist the login after closing the app
     */
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
        view?.findViewById<Button>(R.id.button_sign_out)?.visibility = View.GONE
        view?.findViewById<Button>(R.id.checkBox)?.visibility = View.VISIBLE
        view?.findViewById<Button>(R.id.button_login)?.visibility = View.VISIBLE
    }

    fun setSignOutUi() {
        view?.findViewById<Button>(R.id.button_sign_out)?.visibility = View.VISIBLE
        view?.findViewById<Button>(R.id.checkBox)?.visibility = View.GONE
        view?.findViewById<Button>(R.id.button_login)?.visibility = View.GONE
    }


    /**
     * Updates the Ui by taking into account logging persistance & access privileges.
     */
    fun updateUi() {
        if (prefs!!.getBoolean("hasLogin", false)) {
            view?.findViewById<TextView>(R.id.textView_login_status)?.text =
                prefs?.getString("name", "Not logged in!")
            setSignOutUi()

        } else {
            val loggedUser = presenter.currentUser
            view?.findViewById<TextView>(R.id.textView_login_status)?.text =
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