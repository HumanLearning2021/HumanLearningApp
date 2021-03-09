package com.github.HumanLearning2021.HumanLearningApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView

class ViewImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        var images:DatasetImageModel = intent.getSerializableExtra("label") as DatasetImageModel;

        findViewById<TextView>(R.id.viewLabel).text = images.label;
        findViewById<ImageView>(R.id.viewImage).setImageResource(images.image!!);
    }
}