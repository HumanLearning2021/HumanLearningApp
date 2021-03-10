package com.github.HumanLearning2021.HumanLearningApp

import java.io.Serializable


//TODO : DELETE and replace by CategorizedPicture
class DatasetImageModel : Serializable {

    var category: String?;
    var image: Int?;

    constructor(category: String, image: Int) {
        this.category = category;
        this.image = image;
    }
}