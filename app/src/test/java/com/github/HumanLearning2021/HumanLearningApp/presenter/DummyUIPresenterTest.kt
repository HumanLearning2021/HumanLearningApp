package com.github.HumanLearning2021.HumanLearningApp.presenter

import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatasetInterface
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test
import org.mockito.Mockito


class DummyUIPresenterTest {
    val fork = DummyCategory("Fork")
    val knife = DummyCategory("Knife")
    val spoon = DummyCategory("Spoon")

    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)

    val dummyUri = Mockito.mock(android.net.Uri::class.java)
    val dummyPresenter = DummyUIPresenter(DummyDatasetInterface())

    @Test
    fun getPictureTestEquals() = runBlockingTest {

        assert(dummyPresenter.getPicture("Fork")!!.equals(forkPic))
    }

    @Test
    fun getPictureTestNotEqual() = runBlockingTest {
        assert(!dummyPresenter.getPicture("Fork")!!.equals(knifePic))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getPictureCategoryNotPresent() = runBlockingTest {
        dummyPresenter.getPicture("Plate")
    }

    @Test
    fun getPictureCategoryEmpty() = runBlockingTest {
        dummyPresenter.datasetInterface.putCategory("Plate")
        assertThat(dummyPresenter.getPicture("Plate"), equalTo(null))
    }


    @Test
    fun putAndThenGetWorks() = runBlockingTest {
        dummyPresenter.putPicture(dummyUri, "Fork")
        assertThat(
            dummyPresenter.getPicture("Fork"),
            Matchers.equalTo(DummyCategorizedPicture(fork))
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


