package com.github.HumanLearning2021.HumanLearningApp.view


import android.content.Intent
import android.os.Build
import android.os.Bundle

import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.EXTRA_LEARNING_MODE
import com.github.HumanLearning2021.HumanLearningApp.R
import kotlinx.android.synthetic.main.activity_learning_settings.*

class LearningSettingsActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_settings)

        val intent = Intent(this, LearningActivity::class.java)

        val btPres = findViewById<Button>(R.id.btChoosePresentation)
        val btRep = findViewById<Button>(R.id.btChooseRepresentation)

        btPres.setOnClickListener {
            intent.putExtra(EXTRA_LEARNING_MODE, LearningMode.PRESENTATION)
            startActivity(intent)
        }
        btPres.tooltipText = "Play the game in presentation mode: an image must be classified based " +
                "on an exact copy"

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