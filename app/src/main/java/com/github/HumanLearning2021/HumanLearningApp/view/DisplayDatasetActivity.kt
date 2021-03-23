package com.github.HumanLearning2021.HumanLearningApp.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.presenter.DummyUIPresenter
import kotlinx.coroutines.launch
import java.io.Serializable


class DisplayDatasetActivity : AppCompatActivity() {

    private val fork = DummyCategory("Fork")
    private val knife = DummyCategory("Knife")
    private val spoon = DummyCategory("Spoon")

    private val dummyPresenter = DummyUIPresenter(DummyDatabaseService())

    private val datasetImagesList = ArrayList<CategorizedPicture>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_dataset)
        lifecycleScope.launch {
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(knife.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(spoon.name)!!)

            val displayDatasetAdapter =
                DisplayDatasetAdapter(datasetImagesList, this@DisplayDatasetActivity)

            findViewById<GridView>(R.id.display_dataset_imagesGridView).adapter =
                displayDatasetAdapter

            findViewById<GridView>(R.id.display_dataset_imagesGridView).setOnItemClickListener { 
            adapterView, view, i, l ->
                val intent = Intent(this@DisplayDatasetActivity, DisplayImageActivity::class.java)
                intent.putExtra("display_image_image", (datasetImagesList[i]) as Serializable)
                startActivity(intent)
            }
        }


    }

    class DisplayDatasetAdapter(
        private val images: ArrayList<CategorizedPicture>,
        private val context: Activity
    ) : BaseAdapter() {

        private var layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
            val view =
                view ?: layoutInflater.inflate(R.layout.image_and_category_item, viewGroup, false)

            val imageCat = view?.findViewById<TextView>(R.id.image_and_category_item_imageCategory)
            val imageView = view?.findViewById<ImageView>(R.id.image_and_category_item_imageView)

            imageCat?.text = images[position].category.name
            images[position].displayOn(context, imageView as ImageView)

            return view
        }

        override fun getItem(position: Int): Any {
            return images[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return images.size
        }

    }
}
