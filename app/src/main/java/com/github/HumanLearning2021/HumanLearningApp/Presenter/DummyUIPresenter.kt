package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.*
import java.lang.IllegalArgumentException


/**
 * a class representing a dummy UI presenter
 */
class DummyUIPresenter:UIPresenter {
    private val dataSetInterface: DataSetInterface = DummyDataSetInterface()

<<<<<<< HEAD
    override fun getPicture(categoryString: String): CategorizedPicture =
=======
    override suspend fun getPicture(categoryString: String): CategorizedPicture =
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70
            dataSetInterface.getPicture(categoryString)

}