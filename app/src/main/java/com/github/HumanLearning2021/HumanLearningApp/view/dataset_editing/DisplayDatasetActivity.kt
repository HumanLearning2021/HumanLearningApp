package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ApplicationProvider
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.firestore.CachedFirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DisplayDatasetActivity : AppCompatActivity() {
    @Inject
    @Demo2Database
    lateinit var dbManagement: DatabaseManagement

    private lateinit var categories: Set<Category>
    private lateinit var datasetId: String
    private lateinit var dataset: Dataset

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
                    dbManagement.putPicture(pictureUri, category)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_dataset)

        checkIntentExtras(intent.extras)

        var representativePictures = setOf<CategorizedPicture>()

        if (dbManagement is CachedFirestoreDatabaseManagement) {
            (dbManagement as CachedFirestoreDatabaseManagement).initialize(this.applicationContext)
        }

        lifecycleScope.launch {
            dataset = dbManagement.getDatasetById(datasetId)!!
            findViewById<EditText>(R.id.display_dataset_name).setText(dataset.name)
            categories = dataset.categories
            for (cat in categories) {
                var pictures = dbManagement.getAllPictures(cat)
                if (pictures.isNotEmpty()) {
                    representativePictures =
                        representativePictures.plus(dbManagement.getPicture(cat)!!)
                }
            }

            val displayDatasetAdapter =
                DisplayDatasetAdapter(representativePictures, this@DisplayDatasetActivity)

            findViewById<GridView>(R.id.display_dataset_imagesGridView).adapter =
                displayDatasetAdapter

            setGridViewItemListener()
            setTextChangeListener()
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
                val intent = Intent(this@DisplayDatasetActivity, CategoriesEditingActivity::class.java)
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

    private fun checkIntentExtras(extras: Bundle?) {
        lifecycleScope.launch {
            datasetId = if (extras != null && extras["dataset_id"] is String) {
                intent.getStringExtra("dataset_id")!!
            } else {
                "uEwDkGoGADW4hEJoJ6BA"
            }
        }
    }

    private fun setGridViewItemListener() {
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
    }

    private fun setTextChangeListener() {
        findViewById<EditText>(R.id.display_dataset_name).doAfterTextChanged {
            lifecycleScope.launch {
                dataset = dbManagement.editDatasetName(
                    dataset,
                    findViewById<EditText>(R.id.display_dataset_name).text.toString()
                )
            }
        }
    }
}
