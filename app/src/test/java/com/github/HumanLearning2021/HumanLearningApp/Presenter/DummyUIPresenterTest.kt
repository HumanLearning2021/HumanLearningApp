package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.*

import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test


class DummyUIPresenterTest {
    val fork = DummyCategory("fork")
    val knife = DummyCategory("knife")
    val spoon = DummyCategory("spoon")

    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)


    @Test

    fun getPictureTest() = runBlockingTest {
        val dummyPresenter = DummyUIPresenter()
        assert(dummyPresenter.getPicture("Fork").equals(forkPic))
    }
}

