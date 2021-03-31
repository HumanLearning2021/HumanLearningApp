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
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import java.io.Serializable

class DisplayImageSetActivity : AppCompatActivity() {

    private val categoryImagesList = ArrayList<CategorizedPicture>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image_set)

        val pictures: Set<CategorizedPicture> =
            intent.getSerializableExtra("display_image_set_images") as Set<CategorizedPicture>

        findViewById<TextView>(R.id.display_image_set_name).text =
            (pictures.elementAt(0)).category.name
        for (pic in pictures) {
            categoryImagesList.add(pic)
        }

        val displayImageSetAdapter =
            DisplayImageSetActivity.DisplayImageSetAdapter(
                categoryImagesList,
                this
            )

        findViewById<GridView>(R.id.display_image_set_imagesGridView).adapter =
            displayImageSetAdapter

        findViewById<GridView>(R.id.display_image_set_imagesGridView).setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(this, DisplayImageActivity::class.java)
            intent.putExtra("display_image_image", (categoryImagesList[i]) as Serializable)
            startActivity(intent)
        }
    }

    class DisplayImageSetAdapter(
        private val images: ArrayList<CategorizedPicture>,
        private val context: Activity
    ) : BaseAdapter() {

        private var layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
            val view =
                view ?: layoutInflater.inflate(R.layout.image_item, viewGroup, false)

            val imageView = view?.findViewById<ImageView>(R.id.image_item_imageView)

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