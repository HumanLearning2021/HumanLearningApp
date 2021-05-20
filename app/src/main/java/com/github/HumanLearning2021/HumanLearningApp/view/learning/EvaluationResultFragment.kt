package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentEvaluationResultBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

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
        val successFailureCountPerPhase = args.evaluationResult.successFailureCountPerPhase

        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.setMaxVisibleValueCount(1)
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(true)

        val barEntries = ArrayList<BarEntry>()

        successFailureCountPerPhase.forEachIndexed { index, pair ->
            if (index != 0) {
                val nSuccess = pair.first
                val nFailures = pair.second
                barEntries.add(BarEntry(index.toFloat(), nSuccess.toFloat() / nFailures.toFloat()))
            }
        }

        val barDataset = BarDataSet(barEntries, getString(R.string.evaluation_result_graph_label))
        barDataset.color = R.color.blue
        val barData = BarData(barDataset)
        barData.barWidth = 0.9f
        barChart.data = barData

        binding.learnAgainButton.setOnClickListener {
            findNavController().navigate(EvaluationResultFragmentDirections.actionEvaluationResultFragmentToLearningDatasetSelectionFragment())
        }
    }
}