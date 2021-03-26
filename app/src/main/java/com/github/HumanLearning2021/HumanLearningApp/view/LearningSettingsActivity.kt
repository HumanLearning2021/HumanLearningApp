package com.github.HumanLearning2021.HumanLearningApp.view


import android.content.Intent
import android.os.Build
import android.os.Bundle

import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R


class LearningSettingsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LEARNING_MODE = "com.github.HumanLearning2021.HumanLearningApp.view.EXTRA_LEARNING_MODE"
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_settings)

        val intent = Intent(this, LearningActivity::class.java)

        val btPres = findViewById<Button>(R.id.learningSettings_btChoosePresentation)
        val btRep = findViewById<Button>(R.id.learningSettings_btChooseRepresentation)

        btPres.setOnClickListener {
            intent.putExtra(EXTRA_LEARNING_MODE, LearningMode.PRESENTATION)
            startActivity(intent)
        }
        btPres.tooltipText = getString(R.string.learning_settings_tooltip_presentation)

        btRep.setOnClickListener {
            //TODO: use representative pictures
            null
            /*
            intent.putExtra(EXTRA_LEARNING_MODE, LearningMode.REPRESENTATION)
            startActivity(intent)

             */
        }

        btRep.isClickable = false
        btRep.alpha = .5f;

    }
}

enum class LearningMode {
    PRESENTATION,
    REPRESENTATION;
}