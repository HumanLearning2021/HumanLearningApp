package com.github.HumanLearning2021.HumanLearningApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.Presenter.DummyUIPresenter

class DummyCategorizedPictureTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_categorized_picture_test)

        val dummyPresenter = DummyUIPresenter()
        val fork = dummyPresenter.getPicture("fork")
        val imageView = findViewById<ImageView>(R.id.imageViewUstensil)
        fork.displayOn(imageView)
    }
}