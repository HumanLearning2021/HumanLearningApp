package com.github.HumanLearning2021.HumanLearningApp.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.HumanLearning2021.HumanLearningApp.model.User
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class RoomUserTest {
    private lateinit var db: RoomOfflineDatabase
    private lateinit var userDao: UserDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RoomOfflineDatabase::class.java).build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun getRandomString() = "${UUID.randomUUID()}"
    private fun getRandomUserType() = User.Type.values().toList().shuffled().first()
    private fun getRandomUser() =
        RoomUser(getRandomString(), getRandomUserType(), getRandomString(), getRandomString())

    @Test
    fun insertThenLoadBasicUser() {
        val id = getRandomString()
        val type = getRandomUserType()
        val testUser = RoomUser(id, type, null, null)

        userDao.insertAll(testUser)

        val res = userDao.loadAll().first()

        assertThat(res, equalTo(testUser))
    }

    @Test
    fun insertThenLoadUser() {
        val testUser = getRandomUser()

        userDao.insertAll(testUser)

        val res = userDao.loadAll().first()

        assertThat(res, equalTo(testUser))
    }

    @Test
    fun insertThenLoadUsers() {
        val numberOfUsers = (2..50).random()
        val testUsers = mutableListOf<RoomUser>()
        for (i in 0 until numberOfUsers) {
            testUsers.add(getRandomUser())
        }

        userDao.insertAll(*testUsers.toTypedArray())

        val res = userDao.loadAll()

        assertThat(res, hasSize(numberOfUsers))
        assertThat(res, containsInAnyOrder(*testUsers.toTypedArray()))
    }

    @Test
    fun loadSpecificYieldsCorrectResult() {
        val numberOfUsers = (1..10).random()
        val testUsers = mutableListOf<RoomUser>()
        for (i in 0 until numberOfUsers) {
            testUsers.add(getRandomUser())
        }

        userDao.insertAll(*testUsers.toTypedArray())

        val requestUser = testUsers.random()
        val res = userDao.load(requestUser.userId, requestUser.type)

        assertThat(res, equalTo(requestUser))
    }

    @Test
    fun deleteUserDeletesUser() {
        val numberOfUsers = (1..10).random()
        val testUsers = mutableListOf<RoomUser>()
        for (i in 0 until numberOfUsers) {
            testUsers.add(getRandomUser())
        }

        userDao.insertAll(*testUsers.toTypedArray())

        val deletionUser = testUsers.random()
        userDao.delete(deletionUser)
        val res = userDao.loadAll()

        assertThat(res, not(contains(deletionUser)))
    }

    @Test
    fun loadEmptyDatabaseYieldsEmptyResult() {
        val res = userDao.loadAll()

        assertThat(res, empty())
    }

    @Test
    fun loadNonExistentUserYieldsNoResult() {
        val res = userDao.load(getRandomString(), getRandomUserType())

        assertThat(res, equalTo(null))
    }

    @Test
    fun deleteNonExistingUserDoesNothing() {
        val numberOfUsers = (0..10).random()
        val testUsers = mutableListOf<RoomUser>()
        for (i in 0 until numberOfUsers) {
            testUsers.add(getRandomUser())
        }

        userDao.insertAll(*testUsers.toTypedArray())
        val deletionUser = getRandomUser()
        userDao.delete(deletionUser)
        val res = userDao.loadAll()

        assertThat(res, hasSize(numberOfUsers))
        assertThat(res, containsInAnyOrder(*testUsers.toTypedArray()))
    }

    @Test
    fun updatingUserWorks() {
        val numberOfUsers = (1..10).random()
        val testUsers = mutableListOf<RoomUser>()
        for (i in 0 until numberOfUsers) {
            testUsers.add(getRandomUser())
        }

        userDao.insertAll(*testUsers.toTypedArray())
        val toUpdateUser = testUsers.random()
        val updatedUser =
            RoomUser(toUpdateUser.userId, toUpdateUser.type, getRandomString(), toUpdateUser.email)
        userDao.update(updatedUser)
        val res = userDao.loadAll()

        assertThat(res, hasSize(numberOfUsers))
        assertThat(res, not(contains(toUpdateUser)))
        assertThat(userDao.load(updatedUser.userId, updatedUser.type), equalTo(updatedUser))
    }
}