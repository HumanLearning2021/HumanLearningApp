package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.view.MainFragment
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LearningDatasetSelectionActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_SELECTED_DATASET = "com.github.HumanLearning2021.HumanLearningApp.view.SELECTED_DATASET"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_dataset_selection)

        if (savedInstanceState == null) {
            val fragment = LearningDatasetSelectionFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.learning_dataset_selection_content, fragment)
                .commit()
        }
    }
}