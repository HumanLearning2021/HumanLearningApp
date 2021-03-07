package com.github.HumanLearning2021.HumanLearningApp.Model

interface Model {
    /*
    * TODO: Design choice: keep state of which user currently using the app here,
    *       or else in Presenter and pass it as argument to getPicturesToDisplay, getStatistics
    *       and postStatistic and remove switchUser
     */


    fun getPicturesToDisplay(): PictureSet

    fun getStatistics(): Statistics

    fun postStatistic(picture: CategorizedPicture, wasCorrect: Boolean)

    fun switchUser(user: User)

    fun postPicture(categorizedPicture: CategorizedPicture, dataset: DataSet)

    //TODO: display dataset

}