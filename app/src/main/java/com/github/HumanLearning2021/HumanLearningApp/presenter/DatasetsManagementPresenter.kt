package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.net.Uri
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category

object DatasetsManagementPresenter: DatasetsManagementPresenterInterface {
    override suspend fun putRepresentativePicture(picture: Uri, category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun putRepresentativePicture(picture: CategorizedPicture) {
        TODO("Not yet implemented")
    }

    override fun getDatasetNames(): Set<String> {
        TODO("Not yet implemented")
    }

    override fun editDatasetName(key: String, newName: String) {
        TODO("Not yet implemented")
    }
}