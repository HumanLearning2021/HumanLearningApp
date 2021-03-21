package com.github.HumanLearning2021.HumanLearningApp.presenter

import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test


class DummyUIPresenterTest {
    val fork = DummyCategory("fork")
    val knife = DummyCategory("knife")
    val spoon = DummyCategory("spoon")

    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)


    @Test
    fun getPictureTestEquals() = runBlockingTest {
        val dummyPresenter = DummyUIPresenter()
        assert(dummyPresenter.getPicture("Fork")!!.equals(forkPic))
    }

    @Test
    fun getPictureTestNotEqual() = runBlockingTest {
        val dummyPresenter = DummyUIPresenter()
        assert(!dummyPresenter.getPicture("Fork")!!.equals(knifePic))
    }

    @Test(expected = IllegalArgumentException::class)
    fun getPictureCategoryNotPresent() = runBlockingTest {
        DummyUIPresenter().getPicture("plate")
    }

    @Test
    fun getPictureCategoryEmpty() = runBlockingTest {
        val dummyPresenter = DummyUIPresenter()
        dummyPresenter.dataSetInterface.putCategory("plate")
        assertThat(dummyPresenter.getPicture("plate"), equalTo(null))
    }

    @Test
    fun putAndThenGetWorks() = runBlockingTest {
        val dummyPresenter = DummyUIPresenter()
        dummyPresenter.putPicture(null!!,"fork")
        assertThat(dummyPresenter.getPicture("fork"),
            Matchers.equalTo(DummyCategorizedPicture(fork))
        )
    }

    @Test
    fun putPictureCategoryNotPresent() = runBlockingTest {
        val dummyPresenter = DummyUIPresenter()
        val tablePic = dummyPresenter.putPicture(null!!,"table")
        assertThat(dummyPresenter.getPicture("table"),
            Matchers.equalTo(tablePic)
        )
    }
}

