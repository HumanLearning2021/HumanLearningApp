package com.github.HumanLearning2021.HumanLearningApp.Model

class DummyData:Model {

    override fun getPicturesToDisplay(): PictureSet {
        TODO("Not yet implemented")
    }

    override fun getStatistics(): Statistics {
        TODO("Not yet implemented")
    }

    override fun postStatistic(picture: CategorizedPicture, wasCorrect: Boolean) {
        TODO("Not yet implemented")
    }

    override fun switchUser(user: User) {
        TODO("Not yet implemented")
    }

    override fun postPictureToDataSet(categorizedPicture: CategorizedPicture, dataSet: DataSet) {
        TODO("Not yet implemented")
    }

    override fun browseDataSets(category: Category, creator: User): Set<DataSet> {
        TODO("Not yet implemented")
    }

    override fun selectDataSet(name: String, creator: User) {
        TODO("Not yet implemented")
    }

    override fun createDataSet(name: String) {
        TODO("Not yet implemented")
    }
}