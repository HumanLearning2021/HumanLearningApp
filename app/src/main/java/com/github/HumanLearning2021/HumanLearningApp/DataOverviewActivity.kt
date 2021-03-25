package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class DataOverviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_overview)

    }

    fun  create(view: View) {
        val intent_overview = Intent(this, DataCreationActivity::class.java)
        // visual confirmation to make sure toast message is not displayed over fragment as it is hard to test for toasts
        Toast.makeText(this.context, "switch", Toast.LENGTH_SHORT).show()
        startActivity(intent_overview)

    }
}