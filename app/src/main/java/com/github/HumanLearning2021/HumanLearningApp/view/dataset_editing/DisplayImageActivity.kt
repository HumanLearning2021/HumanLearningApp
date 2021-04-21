package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DisplayImageActivity : AppCompatActivity() {
    @Inject
    @Demo2Database
    lateinit var dbManagement: DatabaseManagement
    private var picture: CategorizedPicture? = null
    private lateinit var datasetId: String
    private lateinit var category: Category

    lateinit var fragment: DisplayImageFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        picture =
            intent.getParcelableExtra("single_picture")
        datasetId = intent.getStringExtra("dataset_id")!!

        if (savedInstanceState == null) {
            fragment = DisplayImageFragment.newInstance(datasetId, picture!!)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.display_image_content, fragment)
                .commit()
        }

    }

    private fun removePicture() {
        var noMorePicturesInThisCategory = false
        lifecycleScope.launch {
            if (dbManagement.getAllPictures(category).isEmpty()) {
                noMorePicturesInThisCategory = true
            }
            dbManagement.removePicture(picture!!)
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
        startActivity(Intent(this, DisplayDatasetActivity::class.java)
            .putExtra("dataset_id", datasetId))
    }

    private fun launchDisplayImageSetActivity() {
        val intent = Intent(this, DisplayImageSetActivity::class.java)
        intent.putExtra(
            "category_of_pictures",
            category
        )
        intent.putExtra("dataset_id", datasetId)
        startActivity(intent)
    }
}
