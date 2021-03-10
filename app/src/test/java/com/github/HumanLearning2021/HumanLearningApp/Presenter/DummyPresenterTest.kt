package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.Admin
import com.github.HumanLearning2021.HumanLearningApp.Model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.Model.Category
import org.junit.Test

import org.junit.Assert.*



class DummyPresenterTest {

    val utensil = Category("Utensil", null)
    val fork = Category("Fork", utensil)
    val knife = Category("Knife", utensil)
    val spoon = Category("Spoon", utensil)
    val admin = Admin("HLStaff")

    val forkPic = CategorizedPicture(com.github.HumanLearning2021.HumanLearningApp.Model.fork)
    val knifePic = CategorizedPicture(com.github.HumanLearning2021.HumanLearningApp.Model.knife)
    val spoonPic = CategorizedPicture(com.github.HumanLearning2021.HumanLearningApp.Model.spoon)

    @Test
    fun getPictureTest() {
        val dummyPresenter = DummyPresenter()

        assert(dummyPresenter.getPicture("Fork").equals(forkPic))


    }
}