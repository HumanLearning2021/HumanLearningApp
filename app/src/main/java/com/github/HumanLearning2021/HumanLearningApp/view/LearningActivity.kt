package com.github.HumanLearning2021.HumanLearningApp.view

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.presenter.LearningPresenter

class LearningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)

        findViewById<ImageView>(R.id.learning_cat_0)
            .setOnDragListener(LearningPresenter.targetOnDragListener)
        findViewById<ImageView>(R.id.learning_cat_1)
            .setOnDragListener(LearningPresenter.targetOnDragListener)
        findViewById<ImageView>(R.id.learning_cat_2)
            .setOnDragListener(LearningPresenter.targetOnDragListener)

        findViewById<ImageView>(R.id.learning_im_to_sort)
                .setOnTouchListener(LearningPresenter::onImageToSortTouched)
    }

//    fun onImageToSortClicked(view: View) = LearningPresenter.onImageToSortClicked(view)
}