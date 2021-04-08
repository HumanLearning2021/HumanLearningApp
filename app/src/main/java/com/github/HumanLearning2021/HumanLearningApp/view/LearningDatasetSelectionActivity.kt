package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.HumanLearning2021.HumanLearningApp.DataCreationActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.view.fragments.DatasetListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LearningDatasetSelectionActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_SELECTED_DATASET = "com.github.HumanLearning2021.HumanLearningApp.view.SELECTED_DATASET"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_dataset_selection)

        val dsListFragment = supportFragmentManager.findFragmentById(R.id.LearningDatasetSelection_dataset_list)
        if (dsListFragment is DatasetListFragment) {
            dsListFragment.selectedDataset.observe(this) {
                Log.d(this.localClassName, "Selected dataset $it")
                startActivity(
                    Intent(this, LearningSettingsActivity::class.java)
                            // add the selected dataset as extra
                        .putExtra(EXTRA_SELECTED_DATASET, it)
                )
            }
        }

    }
}