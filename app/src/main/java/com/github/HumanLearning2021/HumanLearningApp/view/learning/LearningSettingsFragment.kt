package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentLearningSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LearningSettingsFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity
    private val args: LearningSettingsFragmentArgs by navArgs()
    private var _binding: FragmentLearningSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        _binding = FragmentLearningSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.learningSettingsBtChoosePresentation.setOnClickListener {
            val action =
                LearningSettingsFragmentDirections.actionLearningSettingsFragmentToLearningFragment(
                    args.datasetId,
                    LearningMode.PRESENTATION
                )
            findNavController().navigate(action)
        }

        binding.learningSettingsBtChooseRepresentation.setOnClickListener {
            val action =
                LearningSettingsFragmentDirections.actionLearningSettingsFragmentToLearningFragment(
                    args.datasetId,
                    LearningMode.REPRESENTATION
                )
            findNavController().navigate(action)
        }

        binding.learningSettingsBtChoosePresentation.tooltipText =
            getString(R.string.learning_settings_tooltip_presentation)
        binding.learningSettingsBtChooseRepresentation.tooltipText =
            getString(R.string.learning_settings_tooltip_representation)

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

enum class LearningMode {
    PRESENTATION,
    REPRESENTATION,

    /**
     * ComVoor-like evaluation
     */
    EVALUATION;
}

