package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.GlobalDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class DownloadSwitchFragment : Fragment(R.layout.fragment_download_switch) {

    @Inject
    @GlobalDatabaseManagement
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    lateinit var dbMgt: DatabaseManagement

    private lateinit var switch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking { dbMgt = globalDatabaseManagement.accessDatabase("demo2") }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) as ConstraintLayout
        switch = view.getViewById(R.id.download_switch) as SwitchCompat
        switch.isChecked = true //TODO("set depending on database state")
        //setSwitchLogic(switch) TODO("uncomment once implemented")
        return view
    }

    private fun setSwitchLogic(switch: SwitchCompat) {
        TODO("not yet implemented")
    }

    fun isDownloaded(): Boolean = switch.isChecked
}