package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.databinding.ActivityDataCreationBinding
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory

import com.github.HumanLearning2021.HumanLearningApp.model.Category

class DataCreationActivity : AppCompatActivity() {

    private var _binding: ActivityDataCreationBinding? = null
    private val binding get() = _binding!!

    // create an arraylist in which
    // we will store user data
    private var categoryList = ArrayList<DummyCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDataCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAdd.setOnClickListener {
            addNewView()
        }


        /*binding.buttonSubmitList.setOnClickListener {
            saveData()
        }*/


    }


    private fun addNewView() {

        val inflater = LayoutInflater.from(this).inflate(R.layout.row_add_category, null)
        binding.parentLinearLayout.addView(inflater, binding.parentLinearLayout.childCount)

    }

    fun removeView(view:View){
        binding.parentLinearLayout.removeView(view.parent as View)

    }

    // this method will be the building block for linking this activtiy to the camera activity

    /*private fun saveData() {
        categoryList.clear()

        val count = binding.parentLinearLayout.childCount
        var v: View?

        for (i in 0 until count) {
            v = binding.parentLinearLayout.getChildAt(i)

            val categoryName: EditText = v.findViewById(R.id.et_name)

            val category = DummyCategory(categoryName.text.toString())

            // add the data to arraylist
            categoryList.add(category)

           // val intent = Intent(this.context::class.java, DataManagementActivity::class.java)

        }
    }*/





    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

