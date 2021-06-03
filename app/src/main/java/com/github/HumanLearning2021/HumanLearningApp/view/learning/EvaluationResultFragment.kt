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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


/**
 * Fragment used to display the result of an comVoor-like evaluation
 */
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
        barChart.setScaleEnabled(false)
        barChart.description.isEnabled = false

        val barEntries = ArrayList<BarEntry>()

        successFailureCountPerPhase.forEachIndexed { index, pair ->
            if (index != 0) {
                val nSuccess = pair.first
                val nFailures = pair.second
                barEntries.add(
                    BarEntry(
                        index.toFloat(),
                        100f * nSuccess.toFloat() / (nSuccess.toFloat() + nFailures.toFloat())
                    )
                )
            }
        }

        val barDataset = BarDataSet(barEntries, getString(R.string.EvaluationResult_graphLabel))
        barDataset.color = R.color.blue
        val barData = BarData(barDataset)
        barData.barWidth = 0.7f
        barChart.data = barData

        binding.learnAgainButton.setOnClickListener {
            findNavController().navigate(EvaluationResultFragmentDirections.actionEvaluationResultFragmentToLearningDatasetSelectionFragment())
        }

        barChart.axisRight.isEnabled = false

        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 110f
        leftAxis.setDrawGridLines(false)

        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.labelCount = successFailureCountPerPhase.size - 1

        xAxis.valueFormatter = IndexAxisValueFormatter(
            IntRange(
                0,
                successFailureCountPerPhase.size - 1
            ).map {
                if (it == 1)
                    "$it category"
                else
                    "$it categories"
            }.toTypedArray()
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}