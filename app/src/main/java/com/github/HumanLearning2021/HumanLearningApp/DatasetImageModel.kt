package com.github.HumanLearning2021.HumanLearningApp

import java.io.Serializable


//TODO : DELETE and replace by CategorizedPicture
//This class is used to represent an image to be able to represent images in the DisplayDatasetActivity
class DatasetImageModel : Serializable {

    var category: String?
    var image: Int?

    constructor(category: String, image: Int) {
        this.category = category
        this.image = image
    }
}