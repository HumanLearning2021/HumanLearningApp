package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import kotlinx.coroutines.launch

class DisplayImageActivity : AppCompatActivity() {

    private lateinit var picture: CategorizedPicture
    private lateinit var datasetId: String
    private val dBManagement = FirestoreDatabaseManagement("demo2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        picture =
            intent.getParcelableExtra("single_picture")!!
        datasetId = intent.getStringExtra("dataset_id")!!

        findViewById<TextView>(R.id.display_image_viewCategory).text = picture.category.name
        picture.displayOn(this, findViewById(R.id.display_image_viewImage))

        findViewById<ImageButton>(R.id.display_image_delete_button).setOnClickListener{
            removePicture()
        }
    }

    private fun removePicture() {
        var noMorePicturesInThisCategory = false
        lifecycleScope.launch {
            dBManagement.removePicture(picture)
            if (dBManagement.getAllPictures(picture.category).isEmpty()) {
                noMorePicturesInThisCategory = true
            }
            Toast.makeText(this@DisplayImageActivity, getText(R.string.picturehasbeenremoved), Toast.LENGTH_SHORT)
                .show()
            if (noMorePicturesInThisCategory) {
                launchDisplayDatasetActivity()
            } else {
                launchDisplayImageSetActivity()
            }
        }
    }

    private fun launchDisplayDatasetActivity() {
        lifecycleScope.launch {
            dBManagement.removeCategory(picture.category)
        }
        val intent = Intent(this, DisplayDatasetActivity::class.java)
        intent.putExtra("dataset_id", datasetId)
        startActivity(intent)
    }

    private fun launchDisplayImageSetActivity() {
        val intent = Intent(this, DisplayImageSetActivity::class.java)
        intent.putExtra(
            "category_of_pictures",
            picture.category
        )
        intent.putExtra("dataset_id", datasetId)
        startActivity(intent)
    }
}