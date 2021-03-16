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

        val imageView = findViewById<ImageView>(R.id.imageView)
        spoon.displayOn(this, imageView)
    }
}