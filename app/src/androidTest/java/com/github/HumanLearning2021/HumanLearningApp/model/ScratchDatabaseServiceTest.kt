package com.github.HumanLearning2021.HumanLearningApp.model

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DummyDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


abstract class ScratchDatabaseServiceTest {
    protected abstract val db: DatabaseService

    @Test
    fun test_putCategory() = runBlocking {
        val cat = db.putCategory("Poire")
        assertThat(cat, hasName("Poire"))
    }

    @Test
    fun test_putPicture() = runBlocking {
        val cat = db.putCategory("Poire")

        val uri =
            Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/${R.drawable.fork}")
        val pic = db.putPicture(uri, cat)
        assertThat(pic, hasCategory(equalTo(cat)))
    }

    @Test
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

    @Test
    fun test_getUser() = runBlocking {
        val firebaseUser = Firebase.auth.signInAnonymously().await().user!!
        db.updateUser(firebaseUser)
        val user = db.getUser(User.Type.FIREBASE, firebaseUser.uid)
        assertThat(user, notNullValue())
        user!!
        assertThat(user.type, equalTo(User.Type.FIREBASE))
        assertThat(user.uid, equalTo(firebaseUser.uid))
        assertThat(user.displayName, equalTo(firebaseUser.displayName))
        assertThat(user.email, equalTo(firebaseUser.email))
    }

    @Test
    fun test_putStatistic() = runBlocking {
        val event = Event.SUCCESS
        val userId = User.Id("aaaaa", User.Type.TEST)
        val datasetId = "real dataset"
        db.putStatistic(Statistic(Statistic.Id(userId, datasetId), mapOf(event to 42)))
        val statistic = db.getStatistic(userId, datasetId)
        assertThat(statistic?.occurrences, hasEntry(event, 42))
    }
}

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FirestoreScratchDatabaseServiceTest : ScratchDatabaseServiceTest() {

    @Inject
    @ScratchDatabase
    override lateinit var db: DatabaseService

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUpDb() {
        hiltRule.inject()
    }
}

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ScratchDummyDatabaseServiceTest : ScratchDatabaseServiceTest() {
    @Inject
    @DummyDatabase
    override lateinit var db: DatabaseService

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUpDb() {
        hiltRule.inject()
    }
}
