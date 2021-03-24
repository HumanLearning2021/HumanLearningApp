package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.View
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.LearningActivity
import com.github.HumanLearning2021.HumanLearningApp.view.LearningSettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun launchToCameraActivity(view: View) {
        val intent = Intent(this, AddPictureActivity::class.java)
        startActivity(intent)
    }

    fun launchToDisplayDatasetActivity(view: View) {
        val intent = Intent(this, DisplayDatasetActivity::class.java)
        startActivity(intent)
    }

    fun launchToLearningActivity(view: View) {
        val intent = Intent(this, LearningSettingsActivity::class.java)
        startActivity(intent)
    }
}