package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.hilt.DemoDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.testing.HiltAndroidRule
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import javax.inject.Inject


abstract class ScratchDatabaseServiceTest : TestCase() {
    @Inject
    @ScratchDatabase
    lateinit var db: DatabaseService

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    override fun setUp() {
        hiltRule.inject()
    }

    fun test_putCategory() = runBlocking {
        val cat = db.putCategory("Poire")
        assertThat(cat, hasName("Poire"))
    }

    fun test_putPicture() = runBlocking {
        val cat = db.putCategory("Poire")

        val uri =
            Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/${R.drawable.fork}")
        val pic = db.putPicture(uri, cat)
        assertThat(pic, hasCategory(equalTo(cat)))
    }

    fun test_updateUser() = runBlocking {
        val firebaseUser = Firebase.auth.signInAnonymously().await().user!!
        val user = db.updateUser(firebaseUser)
        assertThat(user.type, equalTo(User.Type.FIREBASE))
        assertThat(user.uid, equalTo(firebaseUser.uid))
        assertThat(user.displayName, equalTo(firebaseUser.displayName))
        assertThat(user.email, equalTo(firebaseUser.email))

        // idempotency
        assertThat(db.updateUser(firebaseUser), equalTo(user))
    }

    fun test_getUser() = runBlocking {
        val firebaseUser = Firebase.auth.signInAnonymously().await().user!!
        db.updateUser(firebaseUser)
        val user = db.getUser(User.Type.FIREBASE, firebaseUser.uid)
        assertNotNull(user)
        user!!
        assertThat(user.type, equalTo(User.Type.FIREBASE))
        assertThat(user.uid, equalTo(firebaseUser.uid))
        assertThat(user.displayName, equalTo(firebaseUser.displayName))
        assertThat(user.email, equalTo(firebaseUser.email))
    }
}
