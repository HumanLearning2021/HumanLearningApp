package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_categorized_picture_test)

        val intent = Intent(this, DummyCategorizedPictureTestActivity::class.java)
        startActivity(intent)

    }
}