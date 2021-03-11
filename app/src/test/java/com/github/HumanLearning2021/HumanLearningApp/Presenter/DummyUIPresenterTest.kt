package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.*
<<<<<<< HEAD
=======
import kotlinx.coroutines.test.runBlockingTest
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70
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
<<<<<<< HEAD
    fun getPictureTest() {
=======
    fun getPictureTest() = runBlockingTest {
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70
        val dummyPresenter = DummyUIPresenter()

        assert(dummyPresenter.getPicture("Fork").equals(forkPic))


    }
}