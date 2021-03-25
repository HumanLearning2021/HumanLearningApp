package com.github.HumanLearning2021.HumanLearningApp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

/**
 * A fragment representing a list of Items.
 */
class DatasetOverViewFragment : Fragment(), DatasetOverviewRecyclerViewAdapter.OnItemClickListener {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    //
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dataset_over_view_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                // this creates a vertical scrolling list
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = DatasetOverviewRecyclerViewAdapter(listener =  this@DatasetOverViewFragment)

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
        val intent_onClick = Intent(this.context, DataCreationActivity::class.java)
        Toast.makeText(this.context, "Item $position clicked", Toast.LENGTH_SHORT).show()
        startActivity(intent_onClick)

    }
}