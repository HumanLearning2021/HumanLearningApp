package com.github.HumanLearning2021.HumanLearningApp.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.github.HumanLearning2021.HumanLearningApp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth


class GoogleSignInActivity : AppCompatActivity() {
    val authStateListener = FirebaseAuth.AuthStateListener {
        currentUser = it.currentUser
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)
        Firebase.auth.addAuthStateListener(authStateListener)
    }

    var currentUser: FirebaseUser? = null

    object SignInContract : ActivityResultContract<Unit, FirebaseUser?>() {
        /** Create an intent that can be used for [Activity.startActivityForResult]  */
        override fun createIntent(context: Context, input: Unit?) =
            Intent(context, GoogleSignInActivity::class.java)

        /** Convert result obtained from [Activity.onActivityResult] to O  */
        override fun parseResult(resultCode: Int, intent: Intent?) =
            if (resultCode != Activity.RESULT_OK)
                null
            else
                intent?.extras?.get("result") as FirebaseUser

    }


    override fun finish() {
        val returnIntent = Intent()
        val fragment = findViewById<FragmentContainerView>(R.id.GoogleSignInActivity_GoogleSignInFragment)
        returnIntent.putExtra("result", currentUser)
        setResult(Activity.RESULT_OK, returnIntent)
        super.finish()
    }
}