package com.github.HumanLearning2021.HumanLearningApp.view

import com.github.HumanLearning2021.HumanLearningApp.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject



@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    @Inject
    @Demo2Database
    lateinit var dbMgt: DatabaseManagement
    lateinit var datasetList: List<String>
    lateinit var searchView: SearchView
    lateinit var listView: ListView
    lateinit var list: ArrayList<String>
    lateinit var adapter: ArrayAdapter<*>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        lifecycleScope.launch {
                datasetList = dbMgt.getDatasetNames().toList()
        title = "Look up dataset"
        searchView = findViewById(R.id.searchView)
        listView = findViewById(R.id.listView)
            adapter = ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, datasetList)
            listView.adapter = adapter
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if (datasetList.contains(query)) {
                        adapter.filter.filter(query)
                    } else {
                        Toast.makeText(this@SearchActivity, "No Match found", Toast.LENGTH_LONG)
                            .show()
                    }
                    return false
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    adapter.filter.filter(newText)
                    return false
                }
            })

    }}
}

