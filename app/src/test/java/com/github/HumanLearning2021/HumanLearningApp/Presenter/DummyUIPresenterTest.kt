package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.*
import org.junit.Test


class DummyUIPresenterTest {
    val fork = Category("Fork")
    val knife = Category("Knife")
    val spoon = Category("Spoon")
    val admin = Admin("HLStaff")

    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)


    @Test
    fun getPictureTest() {
        val dummyPresenter = DummyUIPresenter()

        assert(dummyPresenter.getPicture("Fork").equals(forkPic))


    }
}