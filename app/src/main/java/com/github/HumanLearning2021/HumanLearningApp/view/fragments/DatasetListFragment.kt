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
import com.github.HumanLearning2021.HumanLearningApp.hilt.DummyDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A fragment displaying a list of datasets.
 */
@AndroidEntryPoint
class DatasetListFragment : Fragment() {

    @Inject
    @DummyDatabase
    lateinit var dbMgt: DatabaseManagement

    private val mutableSelectedDataset = MutableLiveData<Dataset>()

    /**
     * LiveData representing the Dataset that has been clicked last
     * Add yourself as observer to get notified when the value changes
     * (use `observe` method of LiveData)
     */
    val selectedDataset: LiveData<Dataset> get() = mutableSelectedDataset

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
                        hostActivity = fragActivity,
                        dbMgt = dbMgt,
                    ) {
                        mutableSelectedDataset.value = it
                    }
                }
            }
        }
        return view
    }
}
