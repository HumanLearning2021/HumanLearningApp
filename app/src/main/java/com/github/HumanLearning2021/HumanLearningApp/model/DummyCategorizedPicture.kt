package com.github.HumanLearning2021.HumanLearningApp.model

import android.app.Activity
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.R
import java.lang.IllegalArgumentException

/**
 * A picture part of the dummy data set. Can be of any of the following categories: "fork",
 * "knife", "spoon"
 */
data class DummyCategorizedPicture(override val category: Category) : CategorizedPicture {

    override fun displayOn(activity: Activity, imageView: ImageView) {
        if(category !is DummyCategory) throw IllegalArgumentException("provide a dummy category " +
                "to the class constructor")

        when(category.name){
            "fork" -> imageView.setImageResource(R.drawable.fork)
            "knife" -> imageView.setImageResource(R.drawable.knife)
            "spoon" -> imageView.setImageResource(R.drawable.spoon)
            else -> throw IllegalArgumentException("only spoon, fork, knife can currently be " +
                    "displayed")
        }
    }
}