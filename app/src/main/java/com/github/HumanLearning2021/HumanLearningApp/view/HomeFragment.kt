package com.github.HumanLearning2021.HumanLearningApp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentHomeBinding

/**
 * Fragment hosting all the fragments loaded according to Navigation Component
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonStartLearning.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLearningDatasetSelectionFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

