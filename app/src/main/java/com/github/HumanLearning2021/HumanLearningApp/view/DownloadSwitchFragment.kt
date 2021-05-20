package com.github.HumanLearning2021.HumanLearningApp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.GlobalDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Fragment containing a switch which, if set, will download the production database and remove it if reset
 */
@AndroidEntryPoint
class DownloadSwitchFragment : Fragment(R.layout.fragment_download_switch) {

    @Inject
    @GlobalDatabaseManagement
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @Inject
    @ProductionDatabaseName
    lateinit var dbName: String

    lateinit var dbMgt: DatabaseManagement

    private lateinit var switch: SwitchCompat
    private lateinit var progressIcon: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            dbMgt = globalDatabaseManagement.accessDatabase(
                dbName
            )
        }
    }

    override fun onResume() {
        super.onResume()
        runBlocking {
            dbMgt = globalDatabaseManagement.accessDatabase(
                dbName
            )
        }
        setSwitchState()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) as ConstraintLayout
        switch = view.getViewById(R.id.download_switch) as SwitchCompat
        progressIcon = view.getViewById(R.id.download_progress_icon) as ProgressBar
        setSwitchState()
        setSwitchLogic()
        return view
    }

    private fun setSwitchState() {
        runBlocking {
            switch.isChecked = globalDatabaseManagement.getDownloadedDatabases().contains(
                dbName
            )
        }
    }

    private fun setSwitchLogic() {
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                progressIcon.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    globalDatabaseManagement.downloadDatabase(
                        dbName
                    )
                }.invokeOnCompletion {
                    CoroutineScope(Dispatchers.Main).launch {
                        progressIcon.visibility = View.INVISIBLE
                    }
                }
            } else {
                globalDatabaseManagement.removeDatabaseFromDownloadsAsync(dbName).onAwait
            }
        }
    }
}