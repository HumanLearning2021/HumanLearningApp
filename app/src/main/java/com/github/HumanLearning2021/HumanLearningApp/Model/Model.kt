package com.github.HumanLearning2021.HumanLearningApp.Model

import android.graphics.drawable.Drawable

interface Model {
    /*
    * TODO:
    *  Design choice: keep state of which user currently using the app here,
    *  or else in Presenter and pass it as argument to getPicturesToDisplay, getStatistics
    *  and postStatistic and remove switchUser
     */


    fun getPicturesToDisplay(): PictureSet

    /*
        TODO:
         return Statistics object, or something more primitive, like a Map<String, String>
         representing e.g. <category, score> (see next todo for similar issue)
     */
    fun getStatistics(): Statistics

    fun postStatistic(picture: CategorizedPicture, wasCorrect: Boolean)

    fun postPictureToDataSet(categorizedPicture: CategorizedPicture, dataSetName: String,
                             dataSetCreator: User)


    /*
        TODO:
         really not sure about this one: return a set of dataset, or just set of names,
         or a set of pictures representing each dataset? i.e do the view or the presenter store
         (name, creator), or actual pointer to dataset object? Same problem applies to creator of
         dataset.
     */
    fun browseDataSets(category: Category, creatorName: String): Set<DataSet>


    /*
        TODO:
         should argument be a dataset? (in this case the presenter has access to dataset references)
     */
    fun selectDataSet(dataSetName: String, creatorName: String)

    fun createDataSet(name: String)

    fun switchUser(user: User)

}