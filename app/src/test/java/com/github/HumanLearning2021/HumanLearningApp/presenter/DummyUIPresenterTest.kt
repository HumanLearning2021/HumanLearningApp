
package com.github.HumanLearning2021.HumanLearningApp.presenter
import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
<<<<<<< HEAD
import kotlinx.coroutines.ExperimentalCoroutinesApi
=======
>>>>>>> main
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito


@ExperimentalCoroutinesApi
class DummyUIPresenterTest {
    private val fork = DummyCategory("Fork", null)
    private val knife = DummyCategory("Knife", null)
    private val spoon = DummyCategory("Spoon", null)

    val dummyUri = Mockito.mock(android.net.Uri::class.java)
<<<<<<< HEAD

    private val forkPic = DummyCategorizedPicture(fork, dummyUri)
    private val knifePic = DummyCategorizedPicture(knife, dummyUri)
    private val spoonPic = DummyCategorizedPicture(spoon, dummyUri)

    val dummyDatabaseService = Mockito.mock(DummyDatabaseService::class.java)
    val dummyPresenter = DummyUIPresenter(dummyDatabaseService)
=======
    val dummyPresenter = DummyUIPresenter(DummyDatabaseService())
>>>>>>> main

    @Test
    fun getPictureTestEquals() = runBlockingTest {
        dummyDatabaseService.putCategory(fork.name)
        dummyDatabaseService.putPicture(forkPic.picture, forkPic.category)
        assert(dummyPresenter.getPicture("Fork")!!.equals(forkPic))
    }

    @Test
    fun getPictureTestNotEqual() = runBlockingTest {
        dummyDatabaseService.putCategory(fork.name)
        dummyDatabaseService.putPicture(forkPic.picture, forkPic.category)
        assert(!dummyPresenter.getPicture("Fork")!!.equals(knifePic))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getPictureCategoryNotPresent() = runBlockingTest {
        dummyPresenter.getPicture("Plate")
    }

    @Test
    fun getPictureCategoryEmpty() = runBlockingTest {
        dummyPresenter.databaseService.putCategory("Plate")
        assertThat(dummyPresenter.getPicture("Plate"), equalTo(null))
    }


    @Test
    fun putAndThenGetWorks() = runBlockingTest {
        dummyPresenter.putPicture(dummyUri, "Fork")
        assertThat(
            dummyPresenter.getPicture("Fork"),
            Matchers.equalTo(DummyCategorizedPicture(fork, dummyUri))
        )
    }

    @Test
    fun putPictureCategoryNotPresent() = runBlockingTest {
        val tablePic = dummyPresenter.putPicture(dummyUri, "Table")
        assertThat(
            dummyPresenter.getPicture("Table"),
            Matchers.equalTo(tablePic)
        )
    }


}


