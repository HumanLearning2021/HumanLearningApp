package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DummyDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.presenter.LearningPresenter
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.MainFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LearningActivity : AppCompatActivity() {
    @Inject
    lateinit var learningPresenter: LearningPresenter

    @Inject
    @DummyDatabase
    lateinit var dbMgt: DatabaseManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)

        val dataset =
            intent.getParcelableExtra<Dataset>(LearningDatasetSelectionActivity.EXTRA_SELECTED_DATASET)!!

        val learningMode =
            intent.getSerializableExtra(LearningSettingsFragment.EXTRA_LEARNING_MODE) as LearningMode


        if (savedInstanceState == null) {
            val fragment = LearningFragment.newInstance(dataset, learningMode)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.learning_content, fragment)
                .commit()
        }
    }
}

