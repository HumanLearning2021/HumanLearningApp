package com.github.HumanLearning2021.HumanLearningApp

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 *
 */
class DatasetOverviewRecyclerViewAdapter(
    // replace with DataSet Names sources internally  with getDatasetNames/keys
    private val values: Array<String> = arrayOf("utensils") ,
    private val listener: OnItemClickListener
    )   : RecyclerView.Adapter<DatasetOverviewRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_dataset_over_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = values[position]
        holder.contentView.text = currentItem
    }

    override fun getItemCount(): Int = values.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
            // One can Add Number of instances in the Datatset and/or a representative picture of the dataset
            val contentView: TextView = view.findViewById(R.id.content)

            override fun toString(): String {
                return super.toString() + " '" + contentView.text + "'"
            }

            // use lambdas for a cleaner version
            init {
                view.setOnClickListener(this)
            }
            override fun onClick(v: View){
                val position = adapterPosition
                //checking if position is still valid
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                }
            }

        }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}
