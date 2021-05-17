package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentCategoriesEditingBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesEditingFragment : Fragment() {

    @Inject
    @Demo2Database
    lateinit var dBManagement: DatabaseManagement

    private var _binding: FragmentCategoriesEditingBinding? = null
    private val binding get() = _binding!!

    private var dsCategories = emptySet<Category>()
    private var datasetId: Id? = null
    private lateinit var dataset: Dataset
    private lateinit var removedCategory: Category
    private lateinit var parentActivity: FragmentActivity
    private var new = false
    private val args: CategoriesEditingFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = requireActivity()
        _binding = FragmentCategoriesEditingBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            if (arguments != null) {

                datasetId = args.datasetId
                if (datasetId == null) {
                    new = true
                }
                if (!new) {
                    dataset = dBManagement.getDatasetById(datasetId!!)!!
                    binding.datasetName?.setText(dataset.name)
                    dsCategories = dataset.categories
                    val count = dsCategories.size
                    var v: View?

                    for (i in 0 until count) {
                        addNewView()
                        v = binding.parentLinearLayout.getChildAt(i)
                        val categoryName: EditText =
                            v.findViewById(R.id.data_creation_category_name)
                        categoryName.setText(
                            dsCategories.elementAt(i).name,
                            TextView.BufferType.EDITABLE
                        )
                    }
                    setTextChangeListener()
                } else {
                    for (i in 0 until 3) {
                        addNewView()
                    }
                }

                setButtonsListener()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback.isEnabled = false
        callback.remove()
        _binding = null
    }


    private fun addNewView() {
        val viewToAdd = View.inflate(parentActivity, R.layout.row_add_category, null)
        binding.parentLinearLayout.addView(viewToAdd, binding.parentLinearLayout.childCount)
        viewToAdd.findViewById<ImageButton>(R.id.button_remove)
            .setOnClickListener { removeView(it) }
    }


    private fun removeView(view: View) {
        val categoryName: EditText =
            (view.parent as View).findViewById(R.id.data_creation_category_name)
        lifecycleScope.launch {
            for (i in dsCategories.indices) {
                if (dsCategories.elementAt(i).name == categoryName.text.toString()) {
                    binding.parentLinearLayout.removeView(view.parent as View)
                    removedCategory = dsCategories.elementAt(i)
                    dsCategories = dsCategories.minus(removedCategory)
                    dataset = dBManagement.removeCategoryFromDataset(dataset, removedCategory)
                    break
                }
            }
            binding.parentLinearLayout.removeView(view.parent as View)
        }
    }


    private fun saveData() {
        lifecycleScope.launch {
            val count = binding.parentLinearLayout.childCount
            var v: View?
            var newCategories = dsCategories

            for (i in dsCategories.size until count) {
                v = binding.parentLinearLayout.getChildAt(i)
                val categoryName: EditText = v.findViewById(R.id.data_creation_category_name)
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
                CategoriesEditingFragmentDirections.actionCategoriesEditingFragmentToDisplayDatasetFragment(
                    datasetId!!
                )
            findNavController().navigate(action)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setButtonsListener() {
        binding.buttonAdd.setOnClickListener {
            addNewView()
        }
        binding.buttonSubmitList.setOnClickListener {
            saveData()
        }
    }

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
        inflater.inflate(R.menu.categories_editing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.categories_editing_menu_info -> {
                AlertDialog.Builder(this.context)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(getString(R.string.info))
                    .setMessage(getString(R.string.categoriesEditingInfo))
                    .show()
                true
            }
            else -> {
                true
            }
        }
    }
}


