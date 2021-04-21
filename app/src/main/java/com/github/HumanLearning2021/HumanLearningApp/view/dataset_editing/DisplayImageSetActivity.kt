package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DisplayImageSetActivity : AppCompatActivity() {
    @Inject
    @Demo2Database
    lateinit var dBManagement: DatabaseManagement
    private lateinit var datasetId: String

    lateinit var fragment: DisplayImageSetFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image_set)

        val category =
            intent.getParcelableExtra<Category>("category_of_pictures") as Category
        datasetId = intent.getStringExtra("dataset_id")!!

        if (savedInstanceState == null) {
            fragment = DisplayImageSetFragment.newInstance(datasetId, category!!)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.display_image_set_content, fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        startActivity(
            Intent(this, DisplayDatasetActivity::class.java)
                .putExtra("dataset_id", datasetId))
    }
}
