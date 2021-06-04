package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentLearningSettingsBinding
import com.github.HumanLearning2021.HumanLearningApp.view.FragmentOptionsUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment where the user is presented with a choice of learning modes/settings to choose from
 */
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
    ): View {
        parentActivity = requireActivity()
        _binding = FragmentLearningSettingsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListener(binding.buttonChoosePresentation, LearningMode.PRESENTATION)
        setButtonListener(
            binding.buttonChooseRepresentation,
            LearningMode.REPRESENTATION
        )
        binding.buttonChooseEvaluation?.let {
            setButtonListener(
                it,
                LearningMode.EVALUATION
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.learning_settings_info_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return FragmentOptionsUtil.displayInfoMenu(
            item = item,
            infoItemId = R.id.learning_settings_menu_info,
            title = getString(R.string.info),
            message = getString(R.string.displayLearningSettingsInfo),
            context = this.context
        )
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

