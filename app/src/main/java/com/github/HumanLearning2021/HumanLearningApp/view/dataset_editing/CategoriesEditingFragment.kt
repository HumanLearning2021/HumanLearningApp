package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentCategoriesEditingBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesEditingFragment : Fragment() {

    @Inject
    @Demo2Database
    lateinit var dBManagement: DatabaseManagement

    private var _binding: FragmentCategoriesEditingBinding? = null
    private val binding get() = _binding!!

    private var dsCategories = emptySet<Category>()
    private lateinit var datasetId: String
    private lateinit var dataset: Dataset
    private lateinit var removedCategory: Category
    private lateinit var parentActivity: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        _binding = FragmentCategoriesEditingBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            if (arguments != null) {
                datasetId = arguments?.getString(ARG_DSET_ID) as String
                dataset = dBManagement.getDatasetById(datasetId)!!
                dsCategories = dataset.categories
                val count = dsCategories.size
                var v: View?

                for (i in 0 until count) {
                    addNewView()
                    v = binding.parentLinearLayout.getChildAt(i)
                    val categoryName: EditText = v.findViewById(R.id.data_creation_category_name)
                    categoryName.setText(
                        dsCategories.elementAt(i).name,
                        TextView.BufferType.EDITABLE
                    )
                }

                setButtonsListener()
            }
        }
    }


    private fun addNewView() {

        val inflater = View.inflate(parentActivity, R.layout.row_add_category, null)
        binding.parentLinearLayout.addView(inflater, binding.parentLinearLayout.childCount)

    }

    fun removeView(view: View) {
        val categoryName: EditText =
            (view.parent as View).findViewById(R.id.data_creation_category_name)
        lifecycleScope.launch {
            for (i in dsCategories.indices) {
                if (dsCategories.elementAt(i).name == categoryName.text.toString()) {
                    removedCategory = dsCategories.elementAt(i)
                }
            }
            if (dsCategories.isNotEmpty() && dsCategories.contains(removedCategory)) {
                dsCategories = dsCategories.minus(removedCategory)
                dataset = dBManagement.removeCategoryFromDataset(dataset, removedCategory)
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
                //TODO: Put the generic representative picture
                newCategories = newCategories.plus(cat)

            }

            for (cat in newCategories) {
                dataset = dBManagement.addCategoryToDataset(dataset, cat)
            }

            val intent = Intent(parentActivity, DisplayDatasetActivity::class.java)
            intent.putExtra("dataset_id", datasetId)
            startActivity(intent)
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

        /*
        //TODO: use binding
        parentActivity.findViewById<Button>(R.id.button_remove).setOnClickListener(this::removeView)


         */
    }

    companion object {
        const val ARG_DSET_ID = "dataset_id"

        fun newInstance(dSetId: String): CategoriesEditingFragment {
            val args = Bundle()
            args.putString(ARG_DSET_ID, dSetId)
            val fragment = CategoriesEditingFragment()
            fragment.arguments = args
            return fragment
        }
    }
}