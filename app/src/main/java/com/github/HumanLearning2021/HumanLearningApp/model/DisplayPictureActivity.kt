package com.github.HumanLearning2021.HumanLearningApp.model

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.R

class DisplayPictureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorized_picture_display_test)

        val spoon = DummyCategorizedPicture(DummyCategory("spoon"))
        val knife = DummyCategorizedPicture(DummyCategory("knife"))
        val fork = DummyCategorizedPicture(DummyCategory("fork"))

        val imageViewFork = findViewById<ImageView>(R.id.DisplayFork_imageView)
        val imageViewSpoon = findViewById<ImageView>(R.id.DisplaySpoon_imageView)
        val imageViewKnife = findViewById<ImageView>(R.id.DisplayKnife_imageView)

        spoon.displayOn(this, imageViewSpoon)
        fork.displayOn(this, imageViewFork)
        knife.displayOn(this, imageViewKnife)


    }
}