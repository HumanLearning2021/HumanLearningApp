package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentLearningSettingsBinding
import com.github.HumanLearning2021.HumanLearningApp.view.NavigationUtils
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

    private fun setButtonListener(btn: Button, mode: LearningMode) {
        btn.setOnClickListener {
            findNavController().navigate(
                LearningSettingsFragmentDirections.actionLearningSettingsFragmentToLearningFragment(
                    args.datasetId,
                    learningMode = mode
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListener(binding.learningSettingsBtChoosePresentation, LearningMode.PRESENTATION)
        setButtonListener(
            binding.learningSettingsBtChooseRepresentation,
            LearningMode.REPRESENTATION
        )
        binding.learningSettingsBtChooseEvaluation?.let {
            setButtonListener(
                it,
                LearningMode.EVALUATION
            )
        }

        binding.learningSettingsBtChoosePresentation.tooltipText =
            getString(R.string.learning_settings_tooltip_presentation)
        binding.learningSettingsBtChooseRepresentation.tooltipText =
            getString(R.string.learning_settings_tooltip_representation)
        binding.learningSettingsBtChooseEvaluation?.tooltipText =
            getString(R.string.learning_settings_tooltip_evaluation)

        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    val callback = NavigationUtils.createOnBackPressedCallback(findNavController())

    override fun onDestroyView() {
        super.onDestroyView()
        NavigationUtils.destroyCallback(callback)
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

