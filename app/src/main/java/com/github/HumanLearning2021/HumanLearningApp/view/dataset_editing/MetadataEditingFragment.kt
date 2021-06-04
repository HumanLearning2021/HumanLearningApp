package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentMetadataEditingBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


/**
 * Fragment used to modify the name and categories of the dataset if the user comes
 * from the display dataset fragment or to create a new dataset if the user comes
 * from the datasets overview fragment.
 */
@AndroidEntryPoint
class MetadataEditingFragment : Fragment() {

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @Inject
    @ProductionDatabaseName
    lateinit var dbName: String

    lateinit var dBManagement: DatabaseManagement

    private var _binding: FragmentMetadataEditingBinding? = null
    private val binding get() = _binding!!

    private var dsCategories = emptySet<Category>()
    private var datasetId: Id? = null
    private lateinit var dataset: Dataset
    private lateinit var removedCategory: Category
    private lateinit var parentActivity: FragmentActivity
    private var new = false
    private val args: MetadataEditingFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            dBManagement = globalDatabaseManagement.accessDatabase(
                dbName
            )
        }
    }

    override fun onResume() {
        super.onResume()
        runBlocking {
            dBManagement = globalDatabaseManagement.accessDatabase(
                dbName
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = requireActivity()
        _binding = FragmentMetadataEditingBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            if (arguments != null) {

                datasetId = args.datasetId
                new = datasetId == null

                if (!new) {
                    /**
                     * If the dataset is not new, we add the already existing categories
                     * and the name of the dataset to the layout and set them as non clickable.
                     */
                    dataset = datasetId?.let { dBManagement.getDatasetById(it) }!!
                    binding.datasetName?.setText(dataset.name)
                    dsCategories = dataset.categories
                    val count = dsCategories.size
                    var v: View?

                    for (i in 0 until count) {
                        addNewView()
                        v = binding.parentLinearLayout.getChildAt(i)
                        val categoryName: EditText =
                            v.findViewById(R.id.editText_data_creation_category_name)
                        categoryName.setText(
                            dsCategories.elementAt(i).name,
                            TextView.BufferType.EDITABLE
                        )
                        /**
                         * We decided that it is not possible to edit the name of a category once created.
                         * This choice is due to the chosen implementation of a Category.
                         */
                        categoryName.keyListener = null
                    }
                    setTextChangeListener()
                } else {
                    /**
                     * If the dataset is new, we add 3 empty categories to help the user
                     * to understand that he needs to fill the categories names.
                     */
                    for (i in 0 until 3) {
                        addNewView()
                        val v = binding.parentLinearLayout.getChildAt(i)
                        val categoryName: EditText =
                            v.findViewById(R.id.editText_data_creation_category_name)
                        categoryName.setText(
                            String.format("Category %d", i),
                            TextView.BufferType.EDITABLE
                        )
                    }
                    binding.datasetName?.setText(R.string.DisplayDataset_datasetNameText)
                }

                setButtonsListener()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * This method adds a modifiable category to the list of categories
     * by adding a new view to the list.
     */
    private fun addNewView() {
        val viewToAdd = View.inflate(parentActivity, R.layout.row_add_category, null)
        binding.parentLinearLayout.addView(viewToAdd, binding.parentLinearLayout.childCount)
        viewToAdd.findViewById<ImageButton>(R.id.button_remove)
            .setOnClickListener { removeView(it) }
    }

    /**
     * This method is called after having clicked the delete category button.
     * It removes the view and the category from the dataset if it was already present in the dataset.
     *
     * @param view the view to be removed.
     */
    private fun removeView(view: View) {
        /**
         * We know that this view has a parent since the remove button is
         * inside the view created in addNewView.
         */
        val categoryName: EditText =
            (view.parent as View).findViewById(R.id.editText_data_creation_category_name)
        lifecycleScope.launch {
            /**
             * If there are multiple categories with the same name, remove the last category.
             */
            val categoriesWithNameToBeRemoved =
                dsCategories.filter { it.name == categoryName.text.toString() }
            if (categoriesWithNameToBeRemoved.isNotEmpty()) {
                binding.parentLinearLayout.removeView(view.parent as View)
                removedCategory = categoriesWithNameToBeRemoved.last()
                dsCategories = dsCategories.minus(removedCategory)
                dataset = dBManagement.removeCategoryFromDataset(dataset, removedCategory)
            }
            binding.parentLinearLayout.removeView(view.parent as View)
        }
    }

    /**
     * This method creates the added categories and add them to the dataset.
     * If the dataset is new, it is added to the database.
     * It ends by going to the display dataset fragment.
     */
    private fun saveData() {
        lifecycleScope.launch {
            val count = binding.parentLinearLayout.childCount
            var v: View?
            var newCategories = emptySet<Category>()

            for (i in dsCategories.size until count) {
                v = binding.parentLinearLayout.getChildAt(i)
                val categoryName: EditText =
                    v.findViewById(R.id.editText_data_creation_category_name)
                val cat = dBManagement.putCategory(categoryName.text.toString())
                newCategories = newCategories.plus(cat)
            }

            if (!new) {
                for (cat in newCategories) {
                    dataset = dBManagement.addCategoryToDataset(dataset, cat)
                }
            } else {
                dataset =
                    dBManagement.putDataset(binding.datasetName?.text.toString(), newCategories)
                datasetId = dataset.id
            }

            val action =
                MetadataEditingFragmentDirections.actionCategoriesEditingFragmentToDisplayDatasetFragment(
                    datasetId!!
                )
            findNavController().navigate(action)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Set the listeners for the "Add new category" and the "Save data" buttons.
     * "Add new category" button : Add a new modifiable category for the user
     * "Save data" button : Save the categories into the dataset and go back to Display dataset fragment
     */
    private fun setButtonsListener() {
        binding.buttonAdd.setOnClickListener {
            addNewView()
        }
        binding.buttonSubmitList.setOnClickListener {
            saveData()
        }
    }

    /**
     * Set the listener to modify the name of the dataset whenever the name is modified
     */
    private fun setTextChangeListener() {
        binding.datasetName?.doAfterTextChanged {
            lifecycleScope.launch {
                dataset = dBManagement.editDatasetName(
                    dataset,
                    binding.datasetName?.text.toString()
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.metadata_editing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.categories_editing_menu_info -> {
                AlertDialog.Builder(this.context)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(getString(R.string.MetadataEditing_infoTitle))
                    .setMessage(getString(R.string.MetadataEditing_categoriesEditingInfo))
                    .show()
                true
            }
            else -> {
                true
            }
        }
    }
}


