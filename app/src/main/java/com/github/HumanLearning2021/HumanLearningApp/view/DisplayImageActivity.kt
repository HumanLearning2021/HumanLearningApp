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

    private var picture: CategorizedPicture? = null
    private lateinit var datasetId: String
    private lateinit var dBManagement : FirestoreDatabaseManagement
    private lateinit var dbName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        picture =
            intent.getParcelableExtra("single_picture")
        datasetId = intent.getStringExtra("dataset_id")!!
        dbName = intent.getStringExtra("database_name")!!
        dBManagement = FirestoreDatabaseManagement(dbName)
        if(picture != null) {
            findViewById<TextView>(R.id.display_image_viewCategory).text = picture!!.category.name
            picture!!.displayOn(this, findViewById(R.id.display_image_viewImage))

            findViewById<ImageButton>(R.id.display_image_delete_button).setOnClickListener {
                removePicture()
            }
        }
    }

    private fun removePicture() {
        var noMorePicturesInThisCategory = false
        lifecycleScope.launch {
            dBManagement.removePicture(picture!!)
            if (dBManagement.getAllPictures(picture!!.category).isEmpty()) {
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
        val intent = Intent(this, DisplayDatasetActivity::class.java)
        intent.putExtra("dataset_id", datasetId)
        intent.putExtra("database_name", dbName)
        startActivity(intent)
    }

    private fun launchDisplayImageSetActivity() {
        val intent = Intent(this, DisplayImageSetActivity::class.java)
        intent.putExtra(
            "category_of_pictures",
            picture!!.category
        )
        intent.putExtra("dataset_id", datasetId)
        intent.putExtra("database_name", dbName)
        startActivity(intent)
    }
}