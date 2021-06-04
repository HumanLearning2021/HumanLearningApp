package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentLearningDatasetSelectionBinding
import com.github.HumanLearning2021.HumanLearningApp.view.DownloadSwitchFragment
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListWidget
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment where the user is presented with a choice of datasets to learn on
 */
@AndroidEntryPoint
class LearningDatasetSelectionFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity

    private var _binding: FragmentLearningDatasetSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = requireActivity()
        _binding = FragmentLearningDatasetSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dsListFragment =
            childFragmentManager.findFragmentById(R.id.fragment_dataset_list_learningDatasetSelection)

        if (savedInstanceState == null) {
            childFragmentManager.commit {
                add(R.id.placeholder_for_download_switch, DownloadSwitchFragment())
            }
        }

        if (dsListFragment is DatasetListWidget) {
            dsListFragment.selectedDataset.observe(requireActivity()) {
                Log.d(parentActivity.localClassName, "Selected dataset $it")
                val action =
                    LearningDatasetSelectionFragmentDirections.actionLearningDatasetSelectionFragmentToLearningSettingsFragment(
                        it.id
                    )
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
