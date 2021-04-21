package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatasetsOverviewActivity : AppCompatActivity() {
    lateinit var fragment: DatasetsOverviewFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datasets_overview)

        if (savedInstanceState == null) {
            fragment = DatasetsOverviewFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.datasets_overview_content, fragment)
                .commit()
        }
    }
}