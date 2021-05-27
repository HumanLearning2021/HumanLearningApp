package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentAddPictureBinding
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Id

/**
 * Fragment used to be able to select the way the user wants
 * to add a new picture to the dataset. The two options are
 * selecting an existing picture or take a new picture with the camera.
 */
class AddPictureFragment : Fragment() {

    private lateinit var parentActivity: FragmentActivity
    private var categories = setOf<Category>()
    private lateinit var datasetId: Id

    private val args: AddPictureFragmentArgs by navArgs()

    private var _binding: FragmentAddPictureBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(REQUEST_KEY) { requestKey, bundle ->
            setFragmentResult(DisplayDatasetFragment.REQUEST_KEY, bundle)
            findNavController().popBackStack()
        }
    }

    companion object {
        const val REQUEST_KEY = "AddPictureFragmentRequestKey"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = requireActivity()
        _binding = FragmentAddPictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        datasetId = args.datasetId
        val givenCategories = args.categories.toList()
        categories = categories.plus(givenCategories)


        /**
         * Set the listeners for the two buttons to navigate to the correct fragment
         * to add a new picture to the dataset.
         */
        binding.selectExistingPicture.setOnClickListener {
            val action =
                AddPictureFragmentDirections.actionAddPictureFragmentToSelectPictureFragment(
                    args.categories,
                    datasetId
                )
            findNavController().navigate(action)
        }

        binding.useCamera.setOnClickListener {
            val action = AddPictureFragmentDirections.actionAddPictureFragmentToTakePictureFragment(
                args.categories,
                datasetId
            )
            findNavController().navigate(action)
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
}