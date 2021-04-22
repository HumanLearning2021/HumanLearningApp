package com.github.HumanLearning2021.HumanLearningApp.presenter
import com.github.HumanLearning2021.HumanLearningApp.hilt.DemoDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.DummyDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.*
import javax.inject.Inject





class SearchPresenter @Inject constructor(
    @DemoDatabase
    private val dbMgt: DatabaseManagement) {


}