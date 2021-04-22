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

        datasetId = intent.getStringExtra("dataset_id")!!
        //checkIntentExtras(savedInstanceState)

        if (savedInstanceState == null) {
            fragment = DisplayDatasetFragment.newInstance(datasetId)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.display_dataset_content, fragment)
                .commit()
        }

    }


    // Not using this if else unlike in the original activity. This was breaking the tests. If
    // anyone sees what this is feel free to correct it or let me know
    private fun checkIntentExtras(extras: Bundle?) {
        lifecycleScope.launch {
            datasetId = if (extras != null && extras["dataset_id"] is String) {
                val intent = intent
                intent.getStringExtra("dataset_id")!!
            } else {
                "uEwDkGoGADW4hEJoJ6BA"
            }
        }
    }

}
