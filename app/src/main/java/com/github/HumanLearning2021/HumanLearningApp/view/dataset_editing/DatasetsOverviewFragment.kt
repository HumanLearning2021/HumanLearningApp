package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatasetsOverviewFragment : Fragment() {
    lateinit var parentActivity: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        return inflater.inflate(R.layout.fragment_datasets_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dsListFragment = childFragmentManager.findFragmentById(R.id.dataOverview_fragment)
        if (dsListFragment is DatasetListWidget) {
            dsListFragment.selectedDataset.observe(parentActivity) {
                Log.d("DataOverview activity", "selected ds is  $it")
                // TODO include Dataset (it) with Intent
                startActivity(Intent(parentActivity, CategoriesEditingActivity::class.java))
            }
        }

        parentActivity.findViewById<Button>(R.id.dataOverviewButton).setOnClickListener(this::launchDataCreationActivity)
    }


    fun launchDataCreationActivity(view: View) {
       startActivity(Intent(parentActivity, CategoriesEditingActivity::class.java))
    }
}

