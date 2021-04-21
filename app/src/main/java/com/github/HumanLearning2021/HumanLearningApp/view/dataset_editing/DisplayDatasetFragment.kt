package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DisplayDatasetFragment: Fragment() {
    private lateinit var parentActivity: FragmentActivity

    @Inject
    @Demo2Database
    lateinit var dbManagement: DatabaseManagement

    private lateinit var categories: Set<Category>
    private lateinit var datasetId: String
    private lateinit var dataset: Dataset

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        return inflater.inflate(R.layout.fragment_display_dataset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkArguments()

        var representativePictures = setOf<CategorizedPicture>()

        lifecycleScope.launch {
            dataset = dbManagement.getDatasetById(datasetId)!!
            parentActivity.findViewById<EditText>(R.id.display_dataset_name).setText(dataset.name)
            categories = dataset.categories
            for (cat in categories) {
                var pictures = dbManagement.getAllPictures(cat)
                if (pictures.isNotEmpty()) {
                    representativePictures =
                        representativePictures.plus(dbManagement.getPicture(cat)!!)
                }
            }

            val displayDatasetAdapter =
               DisplayDatasetAdapter(
                    representativePictures,
                    parentActivity
                )

            parentActivity.findViewById<GridView>(R.id.display_dataset_imagesGridView).adapter =
                displayDatasetAdapter

            setGridViewItemListener()
            setTextChangeListener()
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val categoriesArray = ArrayList<Category>(categories)
        return when (item.itemId) {
            R.id.display_dataset_menu_modify_categories -> {
                val intent = Intent(parentActivity, CategoriesEditingActivity::class.java)
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

    private val addPictureContractRegistration =
        registerForActivityResult(AddPictureActivity.AddPictureContract) { resultPair ->
            if (resultPair == null) {
                Toast.makeText(
                    parentActivity,
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

    private fun setGridViewItemListener() {
        parentActivity.findViewById<GridView>(R.id.display_dataset_imagesGridView).setOnItemClickListener { adapterView, view, i, l ->
            val cat = categories.elementAt(i)
            val intent =
                Intent(parentActivity, DisplayImageSetActivity::class.java)
            intent.putExtra(
                "category_of_pictures",
                cat
            )
            intent.putExtra("dataset_id", datasetId)
            startActivity(intent)
        }
    }

    private fun setTextChangeListener() {
        parentActivity.findViewById<EditText>(R.id.display_dataset_name).doAfterTextChanged {
            lifecycleScope.launch {
                dataset = dbManagement.editDatasetName(
                    dataset,
                    parentActivity.findViewById<EditText>(R.id.display_dataset_name).text.toString()
                )
            }
        }
    }

    companion object {
        const val ARG_DSET_ID = "dataset_id"

        fun newInstance(dSetId: String): DisplayDatasetFragment {
            val args = Bundle()
            args.putString(ARG_DSET_ID, dSetId)
            val fragment = DisplayDatasetFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun checkArguments() {
        lifecycleScope.launch {
            datasetId = if (arguments != null && !requireArguments().isEmpty) {
                arguments?.getString(ARG_DSET_ID)!!
            } else {
                "uEwDkGoGADW4hEJoJ6BA"
            }
        }
    }
}