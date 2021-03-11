package com.github.HumanLearning2021.HumanLearningApp

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var callForResult: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        callForResult =
            registerForActivityResult(AddPictureActivity.AddPictureContract) { pair ->
                when (pair) {
                    null -> findViewById<TextView>(R.id.selectedCategory).text = "no result"
                    else -> run {
                        findViewById<TextView>(R.id.selectedCategory).text = pair!!.first
                        findViewById<ImageView>(R.id.pictureTaken).setImageDrawable(
                            Drawable.createFromPath(
                                pair!!.second.path
                            )
                        )
                    }
                }
            }
    }

    fun launchToCameraActivity(view: View) {
        val categories = arrayOf("category1", "category2", "category3")
        callForResult.launch(categories)
    }
}