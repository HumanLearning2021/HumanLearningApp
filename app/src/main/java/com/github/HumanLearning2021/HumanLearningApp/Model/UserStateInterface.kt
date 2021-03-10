package com.github.HumanLearning2021.HumanLearningApp.Model

interface UserStateInterface {

    /*
    TODO:
     return Statistics object, or something more primitive, like a Map<String, String>
     representing e.g. <category, score> (see next todo for similar issue)
 */
    fun getStatistics(): Statistics

    fun postStatistic(picture: CategorizedPicture, wasCorrect: Boolean)
}