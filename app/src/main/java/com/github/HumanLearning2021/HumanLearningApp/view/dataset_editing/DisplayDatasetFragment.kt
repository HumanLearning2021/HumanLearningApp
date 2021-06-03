package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentDisplayDatasetBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

/**
 * Fragment used to display a dataset.
 *
 * The name of the dataset and all the categories linked with their
 * representative picture or the default representative picture are displayed.
 *
 * Possible actions :
 * - Display all the pictures of a category by clicking on this category.
 * - Add a new picture to the dataset by clicking on the add picture menu button.
 * - Modify the dataset name or categories by clicking on the modify dataset menu button.
 * - Delete the dataset by clicking on the delete dataset menu button.
 */
@AndroidEntryPoint
class DisplayDatasetFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @Inject
    @ProductionDatabaseName
    lateinit var dbName: String

    lateinit var dbManagement: DatabaseManagement

    @Inject
    lateinit var imageDisplayer: ImageDisplayer

    private val args: DisplayDatasetFragmentArgs by navArgs()

    private lateinit var categories: Set<Category>
    private lateinit var datasetId: Id
    private lateinit var dataset: Dataset

    private var _binding: FragmentDisplayDatasetBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            dbManagement = globalDatabaseManagement.accessDatabase(
                dbName
            )
        }

        /**
         * Listener to retrieve the new picture and add it to the dataset.
         */
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val pictureUri = bundle.getParcelable<Uri>("pictureUri")
            val chosenCategory = bundle.getParcelable<Category>("chosenCategory")
            lifecycleScope.launch {
                dbManagement.putPicture(pictureUri!!, chosenCategory!!)
                File(pictureUri.path!!).delete()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        runBlocking {
            dbManagement = globalDatabaseManagement.accessDatabase(
                dbName
            )
        }
    }

    companion object {
        const val REQUEST_KEY = "DisplayDatasetFragmentRequestKey"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = requireActivity()
        _binding = FragmentDisplayDatasetBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datasetId = args.datasetId!!

        val representativePictures = ArrayList<Any>()

        lifecycleScope.launch {
            dataset = dbManagement.getDatasetById(datasetId)!!
            (binding.displayDatasetName as TextView).text = dataset.name
            categories = dataset.categories

            /**
             * Collect the representative pictures of the categories or put 0 if
             * the category has no representative picture.
             */
            for (cat in categories) {
                val reprPicture = dbManagement.getRepresentativePicture(cat.id)
                if (reprPicture == null) {
                    representativePictures.add(0)
                } else {
                    representativePictures.add(reprPicture)
                }
            }

            val displayDatasetAdapter =
                DisplayDatasetAdapter(
                    representativePictures,
                    categories,
                )

            binding.displayDatasetImagesGridView.adapter = displayDatasetAdapter

            setGridViewItemListener()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            /**
             * Navigate to the fragment to modify the name and categories of the dataset
             */
            R.id.display_dataset_menu_modify_name_and_categories -> {
                findNavController().navigate(
                    DisplayDatasetFragmentDirections.actionDisplayDatasetFragmentToCategoriesEditingFragment(
                        datasetId
                    )
                )
                true
            }
            /**
             * Ask the user if he really wants to delete the dataset and delete it if it is the case.
             */
            R.id.display_dataset_menu_delete_dataset -> {
                AlertDialog.Builder(this.context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.DisplayDataset_deletingDatasetText))
                    .setMessage(getString(R.string.DisplayDataset_deleteConfirmationMessage))
                    .setPositiveButton(
                        getString(R.string.Yes)
                    ) { _, _ ->
                        lifecycleScope.launch {
                            dbManagement.deleteDataset(datasetId)
                            findNavController().popBackStack()
                        }
                    }
                    .setNegativeButton(getString(R.string.No), null)
                    .show()
                true
            }
            /**
             * Navigate to the add picture fragment to select the way the user wants to add a new picture.
             */
            else -> {
                findNavController().navigate(
                    DisplayDatasetFragmentDirections.actionDisplayDatasetFragmentToAddPictureFragment(
                        categories.toTypedArray(),
                        datasetId
                    )
                )
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.display_dataset_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Adapter of the grid displaying the categories with their representative picture.
     *
     * @param images either 0 if the category has no representative picture of the representative
     * picture of the category.
     * @param categories the categories of the dataset.
     * @constructor Creates an adapter with the given categories and representative pictures.
     */
    inner class DisplayDatasetAdapter(
        private val images: ArrayList<Any>,
        private val categories: Set<Category>,
    ) : BaseAdapter() {

        private var layoutInflater =
            parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view0: View?, viewGroup: ViewGroup?): View {
            /**
             * For each category, display the category's name linked with the representative picture
             * or with the default representative picture.
             */
            val view =
                view0 ?: layoutInflater.inflate(
                    R.layout.image_and_category_item,
                    viewGroup!!,
                    false
                )

            val imageCat = view.findViewById<TextView>(R.id.image_and_category_item_imageCategory)
            val imageView = view.findViewById<ImageView>(R.id.image_and_category_item_imageView)

            val picture = images.elementAt(position)
            if (picture is CategorizedPicture) {
                imageCat?.text = picture.category.name
                with(imageDisplayer) {
                    lifecycleScope.launch {
                        picture.displayOn(imageView as ImageView)
                    }
                }
            } else {
                imageCat?.text = categories.elementAt(position).name
                imageView.setImageResource(R.drawable.default_representative_picture)
            }

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

    /**
     * Listener for all the elements of the grid.
     *
     * Navigate to the display imageSet fragment to display all the pictures of the category.
     */
    private fun setGridViewItemListener() {
        binding.displayDatasetImagesGridView
            .setOnItemClickListener { _, _, i, _ ->
                val category = categories.elementAt(i)
                val action =
                    DisplayDatasetFragmentDirections.actionDisplayDatasetFragmentToDisplayImageSetFragment(
                        datasetId,
                        category
                    )
                findNavController().navigate(action)
            }
    }
}
