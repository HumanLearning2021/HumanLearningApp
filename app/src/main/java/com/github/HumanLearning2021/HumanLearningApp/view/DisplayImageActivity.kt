package com.github.HumanLearning2021.HumanLearningApp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategorizedPicture

class DisplayImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        val image: DummyCategorizedPicture = intent.getSerializableExtra("display_image_image") as DummyCategorizedPicture

        findViewById<TextView>(R.id.display_image_viewCategory).text = image.category.name
        image.displayOn(this, findViewById(R.id.display_image_viewImage))
    }
}