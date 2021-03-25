package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class DataOverviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_overview)

    }

    fun  create(view: View) {
        val intent = Intent(this, DataCreationActivity::class.java)
        startActivity(intent)

    }
}