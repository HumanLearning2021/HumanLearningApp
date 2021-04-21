package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DisplayDatasetActivity : AppCompatActivity() {
    @Inject
    @Demo2Database
    lateinit var dbManagement: DatabaseManagement
    private lateinit var datasetId: String

    lateinit var fragment: DisplayDatasetFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_dataset)

        val extras = intent.extras

        checkIntentExtras(savedInstanceState)

        if (savedInstanceState == null) {
            fragment = DisplayDatasetFragment.newInstance(datasetId)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.display_dataset_content, fragment)
                .commit()
        }

    }

    private fun checkIntentExtras(extras: Bundle?) {
        lifecycleScope.launch {
            datasetId = if (extras != null && extras["dataset_id"] is String) {
                intent.getStringExtra("dataset_id")!!
            } else {
                "uEwDkGoGADW4hEJoJ6BA"
            }
        }
    }

}
