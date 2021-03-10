package com.github.HumanLearning2021.HumanLearningApp.Model

import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.github.HumanLearning2021.HumanLearningApp.R

class DummyCategorizedPicture(category: Category): CategorizedPicture(category) {
    override fun displayOn(imageView: ImageView) {
        when(category.name){
            "Fork" -> imageView.setImageResource()
            "Knife" -> imageView. //TODO
            "Spoon" -> imageView.
        }
    }
}