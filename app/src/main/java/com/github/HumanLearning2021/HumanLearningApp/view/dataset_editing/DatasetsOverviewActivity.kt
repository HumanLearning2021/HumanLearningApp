package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.view.MainActivity
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatasetsOverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datasets_overview)

        val dsListFragment = supportFragmentManager.findFragmentById(R.id.datasetsOverview_fragment)
        if (dsListFragment is DatasetListWidget) {
            dsListFragment.selectedDataset.observe(this) {
                Log.d(this.localClassName, "Selected dataset $it")
                startActivity(
                    Intent(this, DisplayDatasetActivity::class.java)
                        .putExtra("dataset_id", it.id)
                )
            }
        }

        setCreateNewDSButtonListener()
    }

    private fun setCreateNewDSButtonListener() {
        findViewById<Button>(R.id.datasetsOverviewButton).setOnClickListener {
            startActivity(Intent(this, CategoriesEditingActivity::class.java))
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}
