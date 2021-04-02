
package com.github.HumanLearning2021.HumanLearningApp.presenter
import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class DummyUIPresenterTest {
    private val fork = DummyCategory("Fork",  "Fork",null)
    private val knife = DummyCategory("Knife","Knife", null)
    private val spoon = DummyCategory("Spoon", "Spoon",null)

    private val forkPic = DummyCategorizedPicture(fork, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork))
    private val knifePic = DummyCategorizedPicture(knife, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.knife))
    private val spoonPic = DummyCategorizedPicture(spoon, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.spoon))

    private val databaseService = DummyDatabaseService()
    private val dummyPresenter = DummyUIPresenter(databaseService)

    @Test
    fun getPictureTestEquals() = runBlockingTest {
        databaseService.putCategory(fork.name)
        databaseService.putPicture(forkPic.picture, forkPic.category)
        assert(dummyPresenter.getPicture("Fork")!!.equals(forkPic))
    }

    @Test
    fun getPictureTestNotEqual() = runBlockingTest {
        databaseService.putCategory(fork.name)
        databaseService.putPicture(forkPic.picture, forkPic.category)
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
        dummyPresenter.putPicture(Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork), "Fork")
        assertThat(
            dummyPresenter.getPicture("Fork"),
            Matchers.equalTo(DummyCategorizedPicture(fork, Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork)))
        )
    }

    @Test
    fun putPictureCategoryNotPresent() = runBlockingTest {
        val tablePic = dummyPresenter.putPicture(Uri.parse("android.resource://com.github.HumanLearning2021.HumanLearningApp/"+ R.drawable.fork), "Table")
        assertThat(
            dummyPresenter.getPicture("Table"),
            Matchers.equalTo(tablePic)
        )
    }


}


