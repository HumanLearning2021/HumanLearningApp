package com.github.HumanLearning2021.HumanLearningApp.Model

interface Model {
    /*
    * TODO: Design choice: keep state of which learner currently using the app here,
    *       or else in Presenter and pass it as argument to getPicturesToDisplay, getStatistics
    *       and postStatistic and remove switchCurrentLearner
     */

    fun getPicturesToDisplay(): PictureSet

    fun getStatistics(): Statistics

    fun postStatistic(picture: CategorizedPicture, wasCorrect: Boolean)

    fun switchCurrentLearner(learner: Learner)
    
}