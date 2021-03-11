package com.github.HumanLearning2021.HumanLearningApp.Model

import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.github.HumanLearning2021.HumanLearningApp.R

/**
 * A picture part of the dummy data set. Can be of any of the following categories: "fork", "knife", "spoon"
 */
class DummyCategorizedPicture(category: Category): CategorizedPicture(category) {
    override fun displayOn(imageView: ImageView) {
        when(category.name){
            "Fork" -> imageView.setImageResource(R.drawable.fork)
            "Knife" -> imageView.setImageResource(R.drawable.knife)
            "Spoon" -> imageView.setImageResource(R.drawable.spoon)
        }
    }
}