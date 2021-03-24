package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class DataOverviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = "House"
        setContentView(R.layout.activity_data_overview)

    }

    fun  create(view: View) {
        print("create")
        val intent = Intent(this, DataCreationActivity::class.java)
        startActivity(intent)

    }
}