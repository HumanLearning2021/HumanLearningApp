package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningDatasetSelectionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun launchToDisplayDatasetActivity(view: View) {
        val intent = Intent(this, DisplayDatasetActivity::class.java)
        startActivity(intent)
    }

    fun launchToLearningActivity(view: View) {
        val intent = Intent(this, LearningDatasetSelectionActivity::class.java)
        startActivity(intent)
    }
}
