package com.github.HumanLearning2021.HumanLearningApp.view.learning


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R


class LearningSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_settings)

        if (savedInstanceState == null) {
            val fragment = LearningSettingsFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.learning_settings_content, fragment)
                .commit()
        }

    }
}

