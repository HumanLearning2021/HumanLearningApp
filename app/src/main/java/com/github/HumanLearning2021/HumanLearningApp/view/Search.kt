package com.github.HumanLearning2021.HumanLearningApp.view

import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.hilt.DummyDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Search by Dataset Names.
 * TODO : Search by Category or Tag and display all datasets containing that category/Tag
 */

class Search : AppCompatActivity() {
    lateinit var searchView: SearchView
    lateinit var listView: ListView
    lateinit var list: ArrayList<String>
    lateinit var adapter: ArrayAdapter<*>

    @Inject
    @DummyDatabase
    lateinit var dbMgt: DatabaseManagement

    private lateinit var datasetList: List<String>

    init {
        lifecycleScope.launch {
            datasetList = dbMgt.getDatasetNames().toList()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        searchView = findViewById(R.id.searchView)
        listView = findViewById(R.id.listView)

        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datasetList)
        listView.adapter = adapter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (list.contains(query)) {
                    adapter.filter.filter(query)
                } else {
                    Toast.makeText(this@Search, "No Match found", Toast.LENGTH_LONG).show()
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }
}
    }

}