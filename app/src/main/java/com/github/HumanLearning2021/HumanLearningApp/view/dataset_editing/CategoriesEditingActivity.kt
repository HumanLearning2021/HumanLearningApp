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
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesEditingActivity : AppCompatActivity() {

    @Inject
    @Demo2Database
    lateinit var dBManagement: DatabaseManagement

    private var _binding: ActivityCategoriesEditingBinding? = null
    private val binding get() = _binding!!

    private var dsCategories = emptySet<Category>()
    private lateinit var datasetId: String
    private lateinit var dataset: Dataset
    private lateinit var removedCategory: Category
    private var new = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCategoriesEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val extras = intent.extras
            new = extras == null
            if (!new) {
                datasetId = extras!!["dataset_id"] as Id
                dataset = dBManagement.getDatasetById(datasetId)!!
                dsCategories = dataset.categories
                val count = dsCategories.size
                var v: View?

                for (i in 0 until count) {
                    addNewView()
                    v = binding.parentLinearLayout.getChildAt(i)
                    val categoryName: EditText = v.findViewById(R.id.data_creation_category_name)
                    categoryName.setText(
                        dsCategories.elementAt(i).name,
                        TextView.BufferType.EDITABLE
                    )
                }
            }

            setButtonsListener()
        }


    }


    private fun addNewView() {

        val inflater = View.inflate(this, R.layout.row_add_category, null)
        binding.parentLinearLayout.addView(inflater, binding.parentLinearLayout.childCount)

    }

    fun removeView(view: View) {
        val categoryName: EditText =
            (view.parent as View).findViewById(R.id.data_creation_category_name)
        lifecycleScope.launch {
            for (i in dsCategories.indices) {
                if (dsCategories.elementAt(i).name == categoryName.text.toString()) {
                    binding.parentLinearLayout.removeView(view.parent as View)
                    removedCategory = dsCategories.elementAt(i)
                    dsCategories = dsCategories.minus(removedCategory)
                    dataset = dBManagement.removeCategoryFromDataset(dataset, removedCategory)
                    break
                }
            }
            binding.parentLinearLayout.removeView(view.parent as View)
        }
    }

    private fun saveData() {
        lifecycleScope.launch {
            val count = binding.parentLinearLayout.childCount
            var v: View?
            var newCategories = dsCategories

            for (i in dsCategories.size until count) {
                v = binding.parentLinearLayout.getChildAt(i)
                val categoryName: EditText = v.findViewById(R.id.data_creation_category_name)
                val cat = dBManagement.putCategory(categoryName.text.toString())
                newCategories = newCategories.plus(cat)
            }

            if(!new) {
                for (cat in newCategories) {
                    dataset = dBManagement.addCategoryToDataset(dataset, cat)
                }
            } else {
                dataset = dBManagement.putDataset("New Dataset", newCategories)
                datasetId = dataset.id
            }

            val intent = Intent(this@CategoriesEditingActivity, DisplayDatasetActivity::class.java)
            intent.putExtra("dataset_id", datasetId)
            startActivity(intent)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setButtonsListener() {
        binding.buttonAdd.setOnClickListener {
            addNewView()
        }
        binding.buttonSubmitList.setOnClickListener {
            saveData()
        }
    }
}
