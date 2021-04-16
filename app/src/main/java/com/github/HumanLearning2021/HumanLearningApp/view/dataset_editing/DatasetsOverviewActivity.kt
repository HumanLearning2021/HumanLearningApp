package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatasetsOverviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datasets_overview)

        val dsListFragment = supportFragmentManager.findFragmentById(R.id.dataOverview_fragment)
        if (dsListFragment is DatasetListWidget) {
            dsListFragment.selectedDataset.observe(this) {
                Log.d("DataOverview activity", "selected ds is  $it")
                // TODO include Dataset (it) with Intent
                startActivity(Intent(this, CategoriesEditingActivity::class.java))
            }
        }
    }

    fun launchDataCreationActivity(view: View){
        startActivity(Intent(this, CategoriesEditingActivity::class.java))
    }
}