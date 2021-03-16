package com.github.HumanLearning2021.HumanLearningApp.Model

import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.github.HumanLearning2021.HumanLearningApp.R
import java.lang.IllegalArgumentException

/**
 * A picture part of the dummy data set. Can be of any of the following categories: "fork", "knife", "spoon"
 */
class DummyCategorizedPicture(override val category: Category) : CategorizedPicture {

    //TODO: pass activity as argument
    override fun displayOn(imageView: ImageView) {
        if(category !is DummyCategory) throw IllegalArgumentException("provide a dummy category to the class constructor")

        when(category.name){
            "fork" -> imageView.setImageResource(R.drawable.fork)
            "knife" -> imageView.setImageResource(R.drawable.knife)
            "spoon" -> imageView.setImageResource(R.drawable.spoon)
            else -> throw IllegalArgumentException("only spoon, fork, knife are valid category to retrieve from dummy dataset")
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is CategorizedPicture && other.category == category
    }

    override fun hashCode(): Int {
        return 17 + 31*category.hashCode()
    }
}