package com.github.HumanLearning2021.HumanLearningApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.Model.Category
import com.github.HumanLearning2021.HumanLearningApp.Model.DummyCategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.Presenter.DummyPresenter

class DummyCategorizedPictureTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_categorized_picture_test)

        val dummyPresenter = DummyPresenter()
        val fork = dummyPresenter.getPicture("fork")
        val imageView = findViewById<ImageView>(R.id.imageViewUstensil)
        fork.displayOn(imageView)
    }
}