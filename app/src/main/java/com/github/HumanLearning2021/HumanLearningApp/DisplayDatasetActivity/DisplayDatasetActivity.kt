package com.github.HumanLearning2021.HumanLearningApp.DisplayDatasetActivity

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
import com.github.HumanLearning2021.HumanLearningApp.DatasetImageModel
import com.github.HumanLearning2021.HumanLearningApp.R


class DisplayDatasetActivity : AppCompatActivity() {

    var datasetImagesList = ArrayList<DatasetImageModel>();

    var categories = arrayOf(
            "chat",
            "chat",
            "chien"
    )

    var images = intArrayOf(R.drawable.chat1, R.drawable.chat2, R.drawable.chien1);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_dataset)

        for (i in categories.indices) {
            datasetImagesList.add(DatasetImageModel(categories[i], images[i]))
        }

        val customAdapter = DisplayDatasetAdapter(datasetImagesList, this);

        findViewById<GridView>(R.id.display_dataset_imagesGridView).adapter = customAdapter;

        findViewById<GridView>(R.id.display_dataset_imagesGridView).setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(this, DisplayImageActivity::class.java)
            intent.putExtra("display_image_image", datasetImagesList[i])
            startActivity(intent);
        };


    }

    class DisplayDatasetAdapter(
            var images: ArrayList<DatasetImageModel>,
            var context: Context
    ) : BaseAdapter() {

        var layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
            var view = view;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.image_and_category_item, viewGroup, false);
            }

            var imageCat = view?.findViewById<TextView>(R.id.image_and_category_item_imageCategory);
            var imageView = view?.findViewById<ImageView>(R.id.image_and_category_item_imageView);

            imageCat?.text = images[position].category;
            imageView?.setImageResource(images[position].image!!)

            return view!!;
        }

        override fun getItem(position: Int): Any {
            return images[position];
        }

        override fun getItemId(position: Int): Long {
            return position.toLong();
        }

        override fun getCount(): Int {
            return images.size;
        }

    }
}