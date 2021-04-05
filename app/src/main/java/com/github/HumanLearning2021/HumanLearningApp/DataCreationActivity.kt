package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.databinding.ActivityDataCreationBinding
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import kotlinx.coroutines.launch

class DataCreationActivity : AppCompatActivity() {

    private var _binding: ActivityDataCreationBinding? = null
    private val binding get() = _binding!!

    private var categories = emptySet<Category>()
    private val staticDBManagement = DummyDatabaseManagement.staticDummyDatabaseManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDataCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extra = intent.extras
        if (extra != null && extra["dataset_categories"] is ArrayList<*>) {
            val givenCategories = extra["dataset_categories"] as ArrayList<Category>
            categories = categories.plus(givenCategories)
        }

        val count = categories.size
        var v: View?

        for (i in 0 until count) {
            addNewView()
            v = binding.parentLinearLayout.getChildAt(i)

            val categoryName: EditText = v.findViewById(R.id.data_creation_category_name)
            categoryName.setText(categories.elementAt(i).name, TextView.BufferType.EDITABLE)
        }

        binding.buttonAdd.setOnClickListener {
            addNewView()
        }


        binding.buttonSubmitList.setOnClickListener {
            saveData()
        }


    }


    private fun addNewView() {

        val inflater = LayoutInflater.from(this).inflate(R.layout.row_add_category, null)
        binding.parentLinearLayout.addView(inflater, binding.parentLinearLayout.childCount)

    }

    fun removeView(view: View) {
        val categoryName: EditText =
            (view.parent as View).findViewById(R.id.data_creation_category_name)
        val cat = DummyCategory(categoryName.text.toString(), categoryName.text.toString(), null)
        if (categories.contains(cat)) {
            categories = categories.minus(cat)
            lifecycleScope.launch {
                staticDBManagement.removeCategory(cat)
            }
        }
        binding.parentLinearLayout.removeView(view.parent as View)

    }

    private fun saveData() {
        val count = binding.parentLinearLayout.childCount
        var v: View?

        for (i in categories.size until count) {
            v = binding.parentLinearLayout.getChildAt(i)
            val categoryName: EditText = v.findViewById(R.id.data_creation_category_name)
            val category =
                DummyCategory(categoryName.text.toString(), categoryName.text.toString(), null)
            lifecycleScope.launch {
                if (!categories.contains(category)) {
                    //TODO: Uncomment when solution about Representative Picture is found
                    //staticDBManagement.putCategory(categoryName.text.toString())
                }
            }
        }
        val intent = Intent(this, DisplayDatasetActivity::class.java)
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

