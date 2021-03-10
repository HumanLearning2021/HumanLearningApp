package com.github.HumanLearning2021.HumanLearningApp.DisplayDatasetActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.github.HumanLearning2021.HumanLearningApp.DatasetImageModel
import com.github.HumanLearning2021.HumanLearningApp.R

class DisplayImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        var images: DatasetImageModel = intent.getSerializableExtra("display_image_image") as DatasetImageModel;

        findViewById<TextView>(R.id.display_image_viewCategory).text = images.category;
        findViewById<ImageView>(R.id.display_image_viewImage).setImageResource(images.image!!);
    }
}