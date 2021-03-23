package com.github.HumanLearning2021.HumanLearningApp.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.DataCreationActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatasetInterface
import com.github.HumanLearning2021.HumanLearningApp.presenter.DummyUIPresenter
import kotlinx.coroutines.launch
import java.io.Serializable


class DisplayDatasetActivity : AppCompatActivity() {

    private val fork = DummyCategory("Fork")
    private val knife = DummyCategory("Knife")
    private val spoon = DummyCategory("Spoon")

    private val dummyPresenter = DummyUIPresenter(DummyDatasetInterface())

    private val datasetImagesList = ArrayList<CategorizedPicture>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_dataset)
        lifecycleScope.launch {
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(knife.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(spoon.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(spoon.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(spoon.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(spoon.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(spoon.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(spoon.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(spoon.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(spoon.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)
            datasetImagesList.add(dummyPresenter.getPicture(fork.name)!!)


            val displayDatasetAdapter =
                DisplayDatasetAdapter(datasetImagesList, this@DisplayDatasetActivity)

            findViewById<GridView>(R.id.display_dataset_imagesGridView).adapter =
                displayDatasetAdapter

            //TODO: Modify it such that it goes to DisplayImageSetCategory and displays all the images in the dataset that belong to the given category.
            findViewById<GridView>(R.id.display_dataset_imagesGridView).setOnItemClickListener { adapterView, view, i, l ->
                val intent =
                    Intent(this@DisplayDatasetActivity, DisplayImageSetActivity::class.java)
                intent.putExtra("display_image_set_images", (datasetImagesList[i]) as Serializable)
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
                //TODO: Give to the DataCreationActivity the list of the categories of the dataset
                //intent.putExtra("dataset_categories", dummyPresenter.getCategories())
                startActivity(intent)
                true
            }
            else -> {
                Toast.makeText(this, item?.title.toString() + " is Selected", Toast.LENGTH_SHORT)
                    .show()
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
