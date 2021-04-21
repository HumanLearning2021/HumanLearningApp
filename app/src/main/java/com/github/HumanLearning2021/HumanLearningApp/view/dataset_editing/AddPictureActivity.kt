package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing.AddPictureContract.Companion.finishWith
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing.AddPictureContract.Companion.parseArgs

class AddPictureActivity : AppCompatActivity() {
    private lateinit var categories: ArrayList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_picture)
        categories = parseArgs()
        findViewById<Button>(R.id.use_camera).setOnClickListener {
            launchTakePictureActivity()
        }
        findViewById<Button>(R.id.select_existing_picture).setOnClickListener {
            launchSelectPictureActivity()
        }
    }

    private val selectPictureRegistration =
        registerForActivityResult(SelectPictureActivity.Contract) {
            it?.let {
                finishWith(it.first, it.second)
            }
        }
    private val takePictureRegistration =
        registerForActivityResult(TakePictureActivity.Contract) {
            it?.let {
                finishWith(it.first, it.second)
            }
        }

    private fun launchSelectPictureActivity() {
        selectPictureRegistration.launch(categories)
    }

    private fun launchTakePictureActivity() {
        takePictureRegistration.launch(categories)
    }

    companion object {
        val Contract = AddPictureContract(AddPictureActivity::class.java)
    }
}