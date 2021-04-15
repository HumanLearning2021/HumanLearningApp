package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import kotlinx.coroutines.launch

class DisplayImageActivity : AppCompatActivity() {

    private var picture: CategorizedPicture? = null
    private lateinit var datasetId: String
    private lateinit var dBManagement: FirestoreDatabaseManagement
    private lateinit var dbName: String
    private lateinit var category: Category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        picture =
            intent.getParcelableExtra("single_picture")
        datasetId = intent.getStringExtra("dataset_id")!!
        dbName = intent.getStringExtra("database_name")!!
        dBManagement = FirestoreDatabaseManagement(dbName)
        if (picture != null) {
            category = picture!!.category
            findViewById<TextView>(R.id.display_image_viewCategory).text = category.name
            picture!!.displayOn(this, findViewById(R.id.display_image_viewImage))

            findViewById<ImageButton>(R.id.display_image_delete_button).setOnClickListener {
                removePicture()
            }
        }
    }

    private fun removePicture() {
        var noMorePicturesInThisCategory = false
        lifecycleScope.launch {
            if (dBManagement.getAllPictures(category).isEmpty()) {
                noMorePicturesInThisCategory = true
            }
            dBManagement.removePicture(picture!!)
            Toast.makeText(
                this@DisplayImageActivity,
                getText(R.string.picturehasbeenremoved),
                Toast.LENGTH_SHORT
            )
                .show()
            if (noMorePicturesInThisCategory) {
                launchDisplayDatasetActivity()
            } else {
                launchDisplayImageSetActivity()
            }
        }
    }

    private fun launchDisplayDatasetActivity() {
        val intent = Intent(this, DisplayDatasetActivity::class.java)
        intent.putExtra("dataset_id", datasetId)
        intent.putExtra("database_name", dbName)
        startActivity(intent)
    }

    private fun launchDisplayImageSetActivity() {
        val intent = Intent(this, DisplayImageSetActivity::class.java)
        intent.putExtra(
            "category_of_pictures",
            category
        )
        intent.putExtra("dataset_id", datasetId)
        intent.putExtra("database_name", dbName)
        startActivity(intent)
    }
}