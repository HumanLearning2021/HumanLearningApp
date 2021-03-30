package com.github.HumanLearning2021.HumanLearningApp.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.HumanLearning2021.HumanLearningApp.DataCreationActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset

/**
 * A fragment displaying a list of datasets.
 */
class DatasetListFragment : Fragment(), DatasetListRecyclerViewAdapter.OnItemClickListener {

    private val mutableSelectedDataset = MutableLiveData<Dataset>()
    val selectedItem: LiveData<Dataset> get() = mutableSelectedDataset


    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dataset_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                // this creates a vertical scrolling list
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = DatasetListRecyclerViewAdapter(listener = this@DatasetListFragment)

            }
        }
        return view
    }


    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

    }

    override fun onItemClick(position: Int) {
        // link with the right activities later
        val intentOnClick = Intent(this.context, DataCreationActivity::class.java)
        startActivity(intentOnClick)

    }
}