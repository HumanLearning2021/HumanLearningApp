package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DisplayDatasetActivity : AppCompatActivity() {
    @Inject
    @Demo2Database
    lateinit var dbManagement: DatabaseManagement

    private lateinit var categories: Set<Category>
    private lateinit var datasetId: String
    private lateinit var dataset: Dataset

    lateinit var fragment: DisplayDatasetFragment



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_dataset)

        val extras = intent.extras
        if (extras != null) {
            val datasetId = extras["dataset_id"] as String

            if (savedInstanceState == null) {
                fragment = DisplayDatasetFragment.newInstance(datasetId)
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.display_dataset_content, fragment)
                    .commit()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.display_dataset_menu, menu)
        return true
    }

}
