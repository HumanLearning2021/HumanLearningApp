package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.goToLearningButton.setOnClickListener {
            // Use action and not fragment ID in order to get compile-time argument safety
            val action =
                HomeFragmentDirections.actionMainFragmentToLearningDatasetSelectionFragment()
            findNavController().navigate(action)
        }

        binding.goToDatasetsOverviewButton.setOnClickListener {
            val action = HomeFragmentDirections.actionMainFragmentToDatasetsOverviewFragment()
            findNavController().navigate(action)
        }
    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}