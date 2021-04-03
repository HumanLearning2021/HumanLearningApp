package com.github.HumanLearning2021.HumanLearningApp.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import kotlinx.coroutines.launch

/**
 * A fragment displaying a list of datasets.
 */
class DatasetListFragment : Fragment() {

    private val mutableSelectedDataset = MutableLiveData<Dataset>()

    /**
     * LiveData representing the Dataset that has been clicked last
     * Add yourself as observer to get notified when the value changes
     * (use `observe` method of LiveData)
     */
    val selectedDataset: LiveData<Dataset> get() = mutableSelectedDataset

    private val mutableDatasetList = MutableLiveData<List<Dataset>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO replace with real database
        val db = DummyDatabaseManagement.staticDummyDatabaseManagement
        lifecycleScope.launch {
            mutableDatasetList.value = db.getDatasets().toList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dataset_list, container, false)

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = activity?.let { fragActivity ->
                    DatasetListRecyclerViewAdapter(
                        lifecycleScope = lifecycleScope,
                        hostActivity = fragActivity
                    ) {
                        mutableSelectedDataset.value = it
                    }
                }
            }
            // allows RecyclerView to be updated when the dataset list has been loaded
            mutableDatasetList.observe(viewLifecycleOwner) {
                Log.d("DatasetListFragment", "new ds : $it")
                view.adapter?.notifyDataSetChanged()
            }
        }
        return view
    }
}