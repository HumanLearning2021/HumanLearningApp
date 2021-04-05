package com.github.HumanLearning2021.HumanLearningApp.view

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
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.AddPictureActivity
import com.github.HumanLearning2021.HumanLearningApp.DataCreationActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import kotlinx.coroutines.launch
import java.io.Serializable


class DisplayDatasetActivity : AppCompatActivity() {

    private val staticDBManagement = DummyDatabaseManagement.staticDummyDatabaseManagement
    private lateinit var categories : Set<Category>
    private lateinit var datasetId : String
    private lateinit var dataset : Dataset

    private val addPictureContractRegistration =
        registerForActivityResult(AddPictureActivity.AddPictureContract) { resultPair ->
            if (resultPair == null) {
                Toast.makeText(
                    this,
                    "The picture has not been saved in the dataset",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val category = resultPair.first
                val pictureUri = resultPair.second
                lifecycleScope.launch {
                    staticDBManagement.putPicture(pictureUri, category)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_dataset)

        val extra = intent.extras
        if(extra != null && extra["dataset_id"] is String){
            datasetId = intent.getStringExtra("dataset_id")!!
        }else {
            datasetId = "kitchen utensils"
        }

        var representativePictures = setOf<CategorizedPicture>()

        lifecycleScope.launch {
            dataset = staticDBManagement.getDatasetById(datasetId)!!
            categories = staticDBManagement.getCategories()
            for (cat in categories) {
                representativePictures =
                    representativePictures.plus(staticDBManagement.getPicture(cat)!!)
            }
        }

        val displayDatasetAdapter =
            DisplayDatasetAdapter(representativePictures, this@DisplayDatasetActivity)

        findViewById<GridView>(R.id.display_dataset_imagesGridView).adapter =
            displayDatasetAdapter
        findViewById<GridView>(R.id.display_dataset_imagesGridView).setOnItemClickListener { adapterView, view, i, l ->
            val cat = categories.elementAt(i)
            val intent =
                Intent(this@DisplayDatasetActivity, DisplayImageSetActivity::class.java)
            intent.putExtra(
                "category_of_pictures",
                cat
            )
            intent.putExtra("dataset_id", datasetId)
            startActivity(intent)
        }

        findViewById<EditText>(R.id.display_dataset_name).doAfterTextChanged {
            lifecycleScope.launch {
                dataset.editDatasetName(findViewById<EditText>(R.id.display_dataset_name).text.toString())
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.display_dataset_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val categoriesArray = ArrayList<Category>(categories)
        return when (item.itemId) {
            R.id.display_dataset_menu_modify_categories -> {
                val intent = Intent(this@DisplayDatasetActivity, DataCreationActivity::class.java)
                intent.putParcelableArrayListExtra("dataset_categories", categoriesArray)
                intent.putExtra("dataset_id", datasetId)
                startActivity(intent)
                true
            }
            //Clicked on Add new Picture button
            else -> {
                addPictureContractRegistration.launch(categoriesArray)
                true
            }
        }
    }

    class DisplayDatasetAdapter(
        private val images: Set<CategorizedPicture>,
        private val context: Activity
    ) : BaseAdapter() {

        private var layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
            val view =
                view ?: layoutInflater.inflate(R.layout.image_and_category_item, viewGroup!!, false)

            val imageCat = view?.findViewById<TextView>(R.id.image_and_category_item_imageCategory)
            val imageView = view?.findViewById<ImageView>(R.id.image_and_category_item_imageView)

            imageCat?.text = images.elementAt(position).category.name
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
}
