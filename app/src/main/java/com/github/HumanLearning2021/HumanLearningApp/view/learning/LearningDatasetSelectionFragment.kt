package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentLearningDatasetSelectionBinding
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment.DatasetListWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LearningDatasetSelectionFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity

    private var _binding: FragmentLearningDatasetSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        _binding = FragmentLearningDatasetSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dsListFragment =
            childFragmentManager.findFragmentById(R.id.LearningDatasetSelection_dataset_list)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
