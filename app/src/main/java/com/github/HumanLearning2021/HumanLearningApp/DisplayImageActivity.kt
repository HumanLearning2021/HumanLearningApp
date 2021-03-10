package com.github.HumanLearning2021.HumanLearningApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class DisplayImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        var images: DatasetImageModel = intent.getSerializableExtra("image") as DatasetImageModel;

        findViewById<TextView>(R.id.viewCategory).text = images.category;
        findViewById<ImageView>(R.id.viewImage).setImageResource(images.image!!);
    }
}