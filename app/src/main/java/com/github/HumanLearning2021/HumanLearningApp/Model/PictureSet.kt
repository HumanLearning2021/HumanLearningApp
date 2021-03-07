package com.github.HumanLearning2021.HumanLearningApp.Model

import android.graphics.drawable.Drawable

class PictureSet(val mainPicture: CategorizedPicture, val options: Map<CategorizedPicture, Boolean>)

/*

TODO: Design choice: Allows for only one correct answer

class PictureSet(val mainPicture: Drawable, val options: List<Drawable>, val answerIndex: Int) {

}

 */
