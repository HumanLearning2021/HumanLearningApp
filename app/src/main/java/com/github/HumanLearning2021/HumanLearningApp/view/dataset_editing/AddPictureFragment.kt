package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentAddPictureBinding
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentTakePictureBinding
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import kotlinx.coroutines.launch

class AddPictureFragment: Fragment() {

    private lateinit var parentActivity: FragmentActivity
    private var categories = setOf<Category>()
    private lateinit var datasetId: String

    private val args: AddPictureFragmentArgs by navArgs()

    private var _binding: FragmentAddPictureBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        _binding = FragmentAddPictureBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        datasetId = args.datasetId
        val givenCategories = args.categories.toList()
        categories = categories.plus(givenCategories)

        if (args.chosenCategory != null && args.pictureUri != null) {
            val action = AddPictureFragmentDirections.actionAddPictureFragmentToDisplayDatasetFragment(datasetId, args.chosenCategory, args.pictureUri)
            findNavController().navigate(action)
        }

        binding.selectExistingPicture.setOnClickListener{
            val action = AddPictureFragmentDirections.actionAddPictureFragmentToSelectPictureFragment(categories.toTypedArray(), datasetId)
            findNavController().navigate(action)
        }

        binding.useCamera.setOnClickListener {
            val action = AddPictureFragmentDirections.actionAddPictureFragmentToTakePictureFragment(categories.toTypedArray(), datasetId)
            findNavController().navigate(action)
        }

    }
}