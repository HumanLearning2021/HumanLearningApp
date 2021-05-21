package com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.ImageDisplayer
import kotlinx.coroutines.launch
import java.util.*

class DatasetListRecyclerViewAdapter(
    private val imageDisplayer: ImageDisplayer,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val dbMgt: DatabaseManagement,
    private val itemClickedCallback: (Dataset) -> Unit
) : RecyclerView.Adapter<DatasetListRecyclerViewAdapter.ListItemViewHolder>(), Filterable {

    /**
     * Defines the number of categories shown on one ListItemViewHolder
     */
    private val NB_REPRESENTATIVES_SHOWN = 3

    private lateinit var datasetList: List<Dataset>
    private lateinit var originalDatasetList: List<Dataset>

    init {
        lifecycleScope.launch {
            datasetList = dbMgt.getDatasets().toList()
            originalDatasetList = datasetList
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
                    with(imageDisplayer) {
                        repr0?.displayOn(findViewById(R.id.DatasetList_elt_im0))
                        repr1?.displayOn(findViewById(R.id.DatasetList_elt_im1))
                        repr2?.displayOn(findViewById(R.id.DatasetList_elt_im2))
                    }
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

    @OptIn(ExperimentalStdlibApi::class)
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
                val filteredList = originalDatasetList.filter {
                    it.name.lowercase(Locale.getDefault()).startsWith(filterPattern)
                }
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                datasetList = results?.values as ArrayList<Dataset>
                notifyDataSetChanged()
            }
        }
    }
}

