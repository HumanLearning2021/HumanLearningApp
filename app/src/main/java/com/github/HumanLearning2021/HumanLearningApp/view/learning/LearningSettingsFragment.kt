package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.HumanLearning2021.HumanLearningApp.R

class LearningSettingsFragment: Fragment() {
    private lateinit var parentActivity: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        return inflater.inflate(R.layout.fragment_learning_settings, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // we reuse the intent coming from LearningDatasetSelectionActivity that
        // must contain the Dataset that was selected
        val newIntent = Intent(parentActivity.intent).setClass(parentActivity, LearningActivity::class.java)

        val btPres = parentActivity.findViewById<Button>(R.id.learningSettings_btChoosePresentation)
        val btRep = parentActivity.findViewById<Button>(R.id.learningSettings_btChooseRepresentation)

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

    companion object {
        const val EXTRA_LEARNING_MODE =
            "com.github.HumanLearning2021.HumanLearningApp.view.EXTRA_LEARNING_MODE"
    }
}

enum class LearningMode {
    PRESENTATION,
    REPRESENTATION;
}

