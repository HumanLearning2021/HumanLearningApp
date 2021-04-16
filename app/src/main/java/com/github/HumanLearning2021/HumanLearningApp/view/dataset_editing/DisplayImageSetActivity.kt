package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
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
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DisplayImageSetActivity : AppCompatActivity() {
    @Inject
    @Demo2Database
    lateinit var dBManagement: DatabaseManagement

    private var categorizedPicturesList = setOf<CategorizedPicture>()
    private lateinit var datasetId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image_set)

        val category =
            intent.getParcelableExtra<Category>("category_of_pictures") as Category
        datasetId = intent.getStringExtra("dataset_id")!!

        lifecycleScope.launch {
            categorizedPicturesList = dBManagement.getAllPictures(category)
            if (categorizedPicturesList.isNotEmpty()) {
                findViewById<TextView>(R.id.display_image_set_name).text =
                    (categorizedPicturesList.elementAt(0)).category.name

                val displayImageSetAdapter =
                    DisplayImageSetAdapter(
                        categorizedPicturesList,
                        this@DisplayImageSetActivity
                    )

                findViewById<GridView>(R.id.display_image_set_imagesGridView).adapter =
                    displayImageSetAdapter
                setPictureItemListener()
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, DisplayDatasetActivity::class.java)
            .putExtra("dataset_id", datasetId))
    }

    class DisplayImageSetAdapter(
        private val images: Set<CategorizedPicture>,
        private val context: Activity
    ) : BaseAdapter() {

        private var layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
            val view =
                view ?: layoutInflater.inflate(R.layout.image_item, viewGroup, false)

            val imageView = view?.findViewById<ImageView>(R.id.image_item_imageView)

            images.elementAt(position).displayOn(context, imageView as ImageView)

            return view
        }

        override fun getItem(position: Int): Any {
            return images.elementAt(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return images.size
        }

    }

    private fun setPictureItemListener(){
        findViewById<GridView>(R.id.display_image_set_imagesGridView).setOnItemClickListener { adapterView, view, i, l ->
            val intent =
                Intent(this@DisplayImageSetActivity, DisplayImageActivity::class.java)
            intent.putExtra(
                "single_picture",
                (categorizedPicturesList.elementAt(i)) as Parcelable
            )
            intent.putExtra("dataset_id", datasetId)
            startActivity(intent)
        }
    }
}
