package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentDatasetsOverviewBinding
import com.github.HumanLearning2021.HumanLearningApp.view.DownloadSwitchFragment
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListWidget
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment used to display all the datasets of the database.
 * Three possibilities are offered to the user :
 * - Select a dataset to be able to see it or edit it.
 * - Go to the dataset creation fragment by clicking on the corresponding button.
 * - Download the database to be able to use it offline.
 */
@AndroidEntryPoint
class DatasetsOverviewFragment : Fragment() {
    private var _binding: FragmentDatasetsOverviewBinding? = null
    lateinit var parentActivity: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = requireActivity()
        _binding = FragmentDatasetsOverviewBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val dsListFragment =
            childFragmentManager.findFragmentById(R.id.datasetListFragment)

        /**
         * Go to display dataset fragment and display the clicked dataset.
         */
        if (dsListFragment is DatasetListWidget) {
            dsListFragment.selectedDataset.observe(parentActivity) {
                Log.d("DataOverview activity", "selected ds is  $it")
                val action =
                    DatasetsOverviewFragmentDirections.actionDatasetsOverviewFragmentToDisplayDatasetFragment(
                        it.id
                    )
                findNavController().navigate(action)
            }
        }

        /**
         * Display the download switch to be able to download the database.
         */
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                add(R.id.placeholder_for_download_switch, DownloadSwitchFragment())
            }
        }

        /**
         * Button listener to go to the dataset creation fragment when clicked.
         */
        _binding?.createDatasetButton?.setOnClickListener {
            val action =
                DatasetsOverviewFragmentDirections.actionDatasetsOverviewFragmentToCategoriesEditingFragment()
            findNavController().navigate(action)
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback.isEnabled = false
        callback.remove()
        _binding = null

    }
}

