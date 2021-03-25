package com.github.HumanLearning2021.HumanLearningApp

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.view.DisplayDatasetActivity
import com.github.HumanLearning2021.HumanLearningApp.view.LearningActivity


class MainActivity : AppCompatActivity() {
    val addPicture = registerForActivityResult(AddPictureActivity.AddPictureContract) { result ->
        if (result != null) {
            val img = ImageView(this)
            img.setImageDrawable(Drawable.createFromPath(result.second.path))
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(this).apply {
                    setMessage(result.first.name)
                    setPositiveButton("OK"
                    ) { dialog, which -> dialog.dismiss() }.setView(img)
                }
            builder.create().show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun launchToCameraActivity(view: View) {
        addPicture.launch(arrayOf(DummyCategory("window"), DummyCategory("sofa"), DummyCategory("television")))
    }

    fun launchToDisplayDatasetActivity(view: View) {
        val intent = Intent(this, DisplayDatasetActivity::class.java)
        startActivity(intent)
    }

    fun launchToLearningActivity(view: View) {
        val intent = Intent(this, LearningActivity::class.java)
        startActivity(intent)
    }
}
