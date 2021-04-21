package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.ActivityCategoriesEditingBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesEditingActivity : AppCompatActivity() {
    lateinit var fragment: CategoriesEditingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_editing)
        val extras = intent.extras
        if (extras != null) {
            val datasetId = extras["dataset_id"] as String

            if (savedInstanceState == null) {
                fragment = CategoriesEditingFragment.newInstance(datasetId)
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.categories_editing_content, fragment)
                    .commit()
            }
        }
    }

    //this is a quick hack to be able to use it in delete button's XML. Will be changed when introducing navigation component
    fun removeView(view: View) {
        fragment.removeView(view)
    }


}

