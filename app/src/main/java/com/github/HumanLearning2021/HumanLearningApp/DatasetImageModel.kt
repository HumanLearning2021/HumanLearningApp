package com.github.HumanLearning2021.HumanLearningApp

import java.io.Serializable

class DatasetImageModel : Serializable {

    var label:String? = null;
    var image:Int? = null;

    constructor(label:String, image:Int){
        this.label=label;
        this.image=image;
    }
}