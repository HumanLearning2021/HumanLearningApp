package com.github.HumanLearning2021.HumanLearningApp.Model

import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.github.HumanLearning2021.HumanLearningApp.R
import java.lang.IllegalArgumentException

/**
 * A picture part of the dummy data set. Can be of any of the following categories: "fork", "knife", "spoon"
 */
class DummyCategorizedPicture(override val category: Category) : CategorizedPicture {
    override fun displayOn(imageView: ImageView) {
        if(category !is DummyCategory) throw IllegalArgumentException("provide a dummy category to the class constructor")

        when(category.name){
            "Fork" -> imageView.setImageResource(R.drawable.fork)
            "Knife" -> imageView.setImageResource(R.drawable.knife)
            "Spoon" -> imageView.setImageResource(R.drawable.spoon)
            else -> throw IllegalArgumentException("only spoon, fork, knife are valid category to retrieve from dummy dataset")
        }
    }
}