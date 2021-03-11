package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.*
import java.lang.IllegalArgumentException


/**
 * a class representing a dummy UI presenter
 */
class DummyUIPresenter:UIPresenter {
    private val dataSetInterface: DataSetInterface = DummyDataSetInterface()


    override suspend fun getPicture(categoryString: String): CategorizedPicture =
            dataSetInterface.getPicture(categoryString)

}