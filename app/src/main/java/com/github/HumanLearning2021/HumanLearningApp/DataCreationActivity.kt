package com.github.HumanLearning2021.HumanLearningApp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.github.HumanLearning2021.HumanLearningApp.databinding.ActivityDataCreationBinding

class DataCreationActivity : AppCompatActivity() {

    private var _binding: ActivityDataCreationBinding? = null
    private val binding get() = _binding!!

    // create an arraylist in which
    // we will store user data
    private var categoryList = ArrayList<Category>()

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

            val category = Category()
            category.name = categoryName.text.toString()

            // add the data to arraylist
            categoryList.add(category)
        }
    }*/





    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

