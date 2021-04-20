package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LearningDatasetSelectionFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        return inflater.inflate(R.layout.fragment_learning_dataset_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val dsListFragment = childFragmentManager.findFragmentById(R.id.LearningDatasetSelection_dataset_list)
        if (dsListFragment is DatasetListWidget) {
            dsListFragment.selectedDataset.observe(requireActivity()) {
                Log.d(parentActivity.localClassName, "Selected dataset $it")
                startActivity(
                    Intent(parentActivity, LearningSettingsActivity::class.java)
                        // add the selected dataset as extra
                        .putExtra(LearningDatasetSelectionActivity.EXTRA_SELECTED_DATASET, it)
                )
            }
        }
    }
}