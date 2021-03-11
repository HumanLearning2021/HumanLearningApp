package com.github.HumanLearning2021.HumanLearningApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.Presenter.DummyUIPresenter
<<<<<<< HEAD
=======
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70

class DummyCategorizedPictureTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_categorized_picture_test)

        val dummyPresenter = DummyUIPresenter()
<<<<<<< HEAD
        val fork = dummyPresenter.getPicture("fork")
        val imageView = findViewById<ImageView>(R.id.imageViewUstensil)
        fork.displayOn(imageView)
=======
        lifecycleScope.launch {
            val fork = dummyPresenter.getPicture("fork")
            val imageView = findViewById<ImageView>(R.id.imageViewUstensil)
            fork.displayOn(imageView)
        }
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70
    }
}