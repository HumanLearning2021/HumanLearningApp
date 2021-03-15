package com.github.HumanLearning2021.HumanLearningApp.Model

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.R

class CategorizedPictureDisplayTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorized_picture_display_test)

        val fork = DummyCategorizedPicture(DummyCategory("fork"))

        val imageView = findViewById<ImageView>(R.id.imageView)
        fork.displayOn(imageView)
    }
}