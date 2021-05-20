package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentEvaluationResultBinding

class EvaluationResultFragment : Fragment() {
    private var _binding: FragmentEvaluationResultBinding? = null
    private val binding get() = _binding!!
    private val args: EvaluationResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEvaluationResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val barChart = binding.barchartEvaluationResult
        val evaluationResult = args.evaluationResult

        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.setMaxVisibleValueCount(1)
    }
}