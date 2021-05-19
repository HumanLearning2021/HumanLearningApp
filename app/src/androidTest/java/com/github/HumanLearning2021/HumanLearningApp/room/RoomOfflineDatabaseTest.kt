package com.github.HumanLearning2021.HumanLearningApp.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.firebase.ui.auth.AuthUI
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseManagementModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.DatabaseServiceModule
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.User
import com.github.HumanLearning2021.HumanLearningApp.offline.OfflineDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import javax.inject.Inject

@UninstallModules(DatabaseServiceModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RoomOfflineDatabaseTest {
    private val dbName = "some name"
    private lateinit var db: RoomOfflineDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var datasetDao: DatasetDao
    private lateinit var userDao: UserDao
    private lateinit var databaseDao: DatabaseDao
    private lateinit var presenter: AuthenticationPresenter

    @Inject
    @Demo2Database
    lateinit var demo2DbService: DatabaseService

    @Inject
    @ScratchDatabase
    lateinit var scratchInterface: DatabaseService

    @BindValue
    @Demo2Database
    lateinit var demo2DbMgt: DatabaseManagement

    @get:Rule
    val hiltRule = HiltAndroidRule(this)




    @Before
    fun createDb() {
        hiltRule.inject()
        demo2DbMgt = DatabaseManagementModule.provideDemo2Service(demo2DbService)
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RoomOfflineDatabase::class.java).build()
        categoryDao = db.categoryDao()
        datasetDao = db.datasetDao()
        userDao = db.userDao()
        databaseDao = db.databaseDao()
        databaseDao.insertAll(RoomEmptyHLDatabase(dbName))
        presenter = AuthenticationPresenter(AuthUI.getInstance(), scratchInterface)

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun getRandomString() = "${UUID.randomUUID()}"
    private fun getRandomUserType() = User.Type.values().toList().shuffled().first()
    private fun getRandomUser() =
        RoomUser(
            getRandomString(),
            getRandomUserType(),
            getRandomString(),
            getRandomString(),
            false
        )


    @Test
    fun insertDatasetsAndLoadAll() {
        val ds1 = RoomDatasetWithoutCategories("id1", "dataset 1")
        val ds2 = RoomDatasetWithoutCategories("id2", "dataset 2")

        datasetDao.insertAll(ds1, ds2)
        databaseDao.insertAll(
            RoomDatabaseDatasetsCrossRef(dbName, ds1.datasetId),
            RoomDatabaseDatasetsCrossRef(dbName, ds2.datasetId)
        )

        val res = databaseDao.loadByName(dbName)!!.datasets

        assertThat(res.size, equalTo(2))
        assert(res.contains(ds1))
        assert(res.contains(ds2))
    }

    @Test
    fun setAdminAccessOfflineWorks() {
        runBlocking {
            val firebaseUser = Firebase.auth.signInAnonymously().await().user!!
            presenter.onSuccessfulLogin(false)
            val offlineDBS = OfflineDatabaseService("testDB",ApplicationProvider.getApplicationContext<Context>(),db)
            var offlineUser = offlineDBS.updateUser(firebaseUser)
            assertThat(offlineUser.isAdmin, equalTo(false))
            offlineUser = offlineDBS.setAdminAccess(firebaseUser,true)
            assertThat(offlineUser.isAdmin, equalTo(true))
            assertThat(offlineDBS.checkIsAdmin(offlineUser),equalTo(true))
        }

    }
}