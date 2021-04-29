package com.github.HumanLearning2021.HumanLearningApp.view.learning


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R


class LearningSettingsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LEARNING_MODE =
            "com.github.HumanLearning2021.HumanLearningApp.view.EXTRA_LEARNING_MODE"
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_learning_settings)

        // we reuse the intent coming from LearningDatasetSelectionActivity that
        // must contain the Dataset that was selected
        val newIntent = Intent(intent).setClass(this, LearningActivity::class.java)

        val btPres = findViewById<Button>(R.id.learningSettings_btChoosePresentation)
        val btRep = findViewById<Button>(R.id.learningSettings_btChooseRepresentation)

        btPres.setOnClickListener {
            newIntent.putExtra(EXTRA_LEARNING_MODE, LearningMode.PRESENTATION)
            startActivity(newIntent)
        }
        btRep.setOnClickListener {
            newIntent.putExtra(EXTRA_LEARNING_MODE, LearningMode.REPRESENTATION)
            startActivity(newIntent)
        }

        btPres.tooltipText = getString(R.string.learning_settings_tooltip_presentation)
        btRep.tooltipText = getString(R.string.learning_settings_tooltip_representation)
    }
}

enum class LearningMode {
    PRESENTATION,
    REPRESENTATION;
}