package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.databinding.ActivityDataCreationBinding
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import kotlinx.coroutines.launch

class DataCreationActivity : AppCompatActivity() {

    private var _binding: ActivityDataCreationBinding? = null
    private val binding get() = _binding!!

    private var dsCategories = emptySet<Category>()
    private lateinit var dbName :String
    private lateinit var dBManagement : FirestoreDatabaseManagement
    private lateinit var datasetId : String
    private lateinit var dataset : Dataset

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDataCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val extra = intent.extras
            if(extra != null) {
                datasetId = extra["dataset_id"] as String
                dbName = extra["database_name"] as String
                dBManagement = FirestoreDatabaseManagement(dbName)

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

                binding.buttonAdd.setOnClickListener {
                    addNewView()
                }


                binding.buttonSubmitList.setOnClickListener {
                    saveData()
                }
            }
        }


    }


    private fun addNewView() {

        val inflater = LayoutInflater.from(this).inflate(R.layout.row_add_category, null)
        binding.parentLinearLayout.addView(inflater, binding.parentLinearLayout.childCount)

    }

    fun removeView(view: View) {
        val categoryName: EditText =
            (view.parent as View).findViewById(R.id.data_creation_category_name)
        lifecycleScope.launch {
            val cat = dBManagement.getCategoryByName(categoryName.text.toString())
            if (dsCategories.isNotEmpty() && dsCategories.contains(cat.first())) {
                dsCategories = dsCategories.minus(cat)
                dataset = dBManagement.removeCategoryFromDataset(dataset, cat.first())
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
                //TODO: Put the generic representative picture

                newCategories = newCategories.plus(cat)

            }

            for (cat in newCategories) {
                dataset = dBManagement.addCategoryToDataset(dataset, cat)
            }

            val intent = Intent(this@DataCreationActivity, DisplayDatasetActivity::class.java)
            intent.putExtra("dataset_id", datasetId)
            intent.putExtra("database_name", dbName)
            startActivity(intent)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

