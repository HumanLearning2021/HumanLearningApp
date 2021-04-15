package com.github.HumanLearning2021.HumanLearningApp.view.fragments

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.firestore.FirestoreDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import kotlinx.coroutines.launch

class DatasetListRecyclerViewAdapter(
    private val hostActivity: Activity,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val itemClickedCallback: (Dataset) -> Unit
) : RecyclerView.Adapter<DatasetListRecyclerViewAdapter.ListItemViewHolder>() {

    /**
     * Defines the number of categories shown on one ListItemViewHolder
     */
    private val NB_REPRESENTATIVES_SHOWN = 3

    // TODO Use injection with Hilt!
    private val dbMgt: DatabaseManagement = DummyDatabaseManagement(DummyDatabaseService())

    private lateinit var datasetList: List<Dataset>

    init {
        lifecycleScope.launch {
            datasetList = dbMgt.getDatasets().toList()
            notifyDataSetChanged()
        }
    }

    /**
     * Gets called when a new view holder is needed
     * (for example when the user scrolls to reveal more items)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_dataset_list_elt, parent, false)
        return ListItemViewHolder(view)
    }

    /**
     * Gets called to allow the ViewHolder to be bound to its data, given its position
     */
    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        if (::datasetList.isInitialized) {
            val ds = datasetList[position]
            holder.itemView.apply {
                findViewById<TextView>(R.id.DatasetList_elt_text).text = ds.name
                setRepresentativePictures(this, ds)

                // bind the dataset so that the holder can call `itemClickedCallback`
                // with the appropriate dataset
                holder.bindDataset(ds)
            }
        }
    }

    private fun setRepresentativePictures(view: View, ds: Dataset) {
        val catList = ds.categories.toList()
        if (catList.size >= NB_REPRESENTATIVES_SHOWN) {
            lifecycleScope.launch {
                val repr0 = dbMgt.getRepresentativePicture(catList[0].id)
                val repr1 = dbMgt.getRepresentativePicture(catList[1].id)
                val repr2 = dbMgt.getRepresentativePicture(catList[2].id)

                with(view) {
                    repr0?.displayOn(hostActivity, findViewById(R.id.DatasetList_elt_im0))
                    repr1?.displayOn(hostActivity, findViewById(R.id.DatasetList_elt_im1))
                    repr2?.displayOn(hostActivity, findViewById(R.id.DatasetList_elt_im2))
                }
            }
        }
    }

    override fun getItemCount(): Int =
        if (::datasetList.isInitialized) {
            datasetList.size
        } else {
            0
        }

    inner class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindDataset(ds: Dataset) {
            itemView.setOnClickListener { itemClickedCallback(ds) }
        }
    }
}

