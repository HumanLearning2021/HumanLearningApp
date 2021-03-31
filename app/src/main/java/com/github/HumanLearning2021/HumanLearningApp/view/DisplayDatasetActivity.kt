package com.github.HumanLearning2021.HumanLearningApp.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.AddPictureActivity
import com.github.HumanLearning2021.HumanLearningApp.DataCreationActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import kotlinx.coroutines.launch
import java.io.Serializable


class DisplayDatasetActivity : AppCompatActivity() {

    private val databaseService = DummyDatabaseService()
    private lateinit var datasetName : String

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
                    databaseService.putPicture(pictureUri, category)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_dataset)

        val extra = intent.extras
        if(extra != null && extra["dataset_overview_dataset_name"] is String){
            datasetName = intent.getStringExtra("dataset_overview_dataset_name")!!
        }else {
            datasetName = "kitchen utensils"
        }

        val representativePictures = ArrayList<CategorizedPicture>()

        lifecycleScope.launch {
            val dataset = databaseService.getDataset(datasetName!!)
            val categories = databaseService.getCategories()
            for (cat in categories) {
                representativePictures.add(databaseService.getPicture(cat)!!)
            }

            val displayDatasetAdapter =
                DisplayDatasetAdapter(representativePictures, this@DisplayDatasetActivity)

            findViewById<GridView>(R.id.display_dataset_imagesGridView).adapter =
                displayDatasetAdapter
            findViewById<GridView>(R.id.display_dataset_imagesGridView).setOnItemClickListener { adapterView, view, i, l ->
                val cat = categories.elementAt(i)
                //TODO: REPLACE BY getAllPictures WHEN AVAILABLE
                val allPictures = databaseService.pictures
                val catPictures: MutableSet<CategorizedPicture> = mutableSetOf()
                for (p in allPictures) {
                    if (p.category == cat) {
                        catPictures.add(p)
                    }
                }
                val intent =
                    Intent(this@DisplayDatasetActivity, DisplayImageSetActivity::class.java)
                intent.putExtra(
                    "display_image_set_images",
                    (catPictures) as Serializable
                )
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.display_dataset_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item?.itemId) {
            R.id.display_dataset_menu_modify_categories -> {
                val intent = Intent(this@DisplayDatasetActivity, DataCreationActivity::class.java)
                lifecycleScope.launch {
                    intent.putExtra("dataset_categories", databaseService.getCategories() as Serializable)
                }
                startActivity(intent)
                true
            }
            //Clicked on Add new Picture button
            else -> {
                lifecycleScope.launch {
                    val categories = databaseService.getCategories()
                    addPictureContractRegistration.launch(categories)
                }
                true
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
                view ?: layoutInflater.inflate(R.layout.image_and_category_item, viewGroup!!, false)

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
