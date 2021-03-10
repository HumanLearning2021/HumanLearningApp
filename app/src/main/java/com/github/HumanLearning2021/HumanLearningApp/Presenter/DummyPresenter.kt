package com.github.HumanLearning2021.HumanLearningApp.Presenter

import com.github.HumanLearning2021.HumanLearningApp.Model.*
import java.lang.IllegalArgumentException

class DummyPresenter:UIPresenter {
    private val dataSetInterface: DataSetInterface = DummyDataSetInterface()

    override fun getPicture(categoryString: String): CategorizedPicture =
            dataSetInterface.getPicture(categoryString)
}