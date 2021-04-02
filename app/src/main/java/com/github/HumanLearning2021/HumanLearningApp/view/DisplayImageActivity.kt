package com.github.HumanLearning2021.HumanLearningApp.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture

class DisplayImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        val image: CategorizedPicture =
            intent.getParcelableExtra("display_image_image")!!

        findViewById<TextView>(R.id.display_image_viewCategory).text = image.category.name
        image.displayOn(this, findViewById(R.id.display_image_viewImage))
    }

    fun removePicture(view: View) {
        //TODO: Implement : remove picture from the list and send intent to the previous activity with the updated list
        Toast.makeText(this, getText(R.string.picturehasbeenremoved), Toast.LENGTH_SHORT)
            .show()
    }
}