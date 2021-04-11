package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.view.fragments.DatasetListFragment

class DataOverviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_overview)

        val dsListFragment = supportFragmentManager.findFragmentById(R.id.dataOverview_fragment)
        if (dsListFragment is DatasetListFragment) {
            dsListFragment.selectedDataset.observe(this) {
                Log.d("DataOverview activity", "selected ds is  $it")
                // TODO include Dataset (it) with Intent
                startActivity(Intent(this, DataCreationActivity::class.java))
            }
        }
    }

    fun launchDataCreationActivity(view: View){
        startActivity(Intent(this, DataCreationActivity::class.java))
    }
}