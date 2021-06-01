package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentAddPictureBinding
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import com.github.HumanLearning2021.HumanLearningApp.view.NavigationUtils.createOnBackPressedCallback
import com.github.HumanLearning2021.HumanLearningApp.view.NavigationUtils.destroyCallback
import java.io.File
import java.io.FileOutputStream
import java.util.*


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
        private const val imageCompressionQuality = 25
        const val REQUEST_KEY = "AddPictureFragmentRequestKey"

        fun applyImageSizeReduction(bitmap: Bitmap, context: Context): Uri {
            // random string as name as a fallback in case the app crashes and the image file does
            // not get deleted, avoids crashes during image taking process.
            val file = File(
                context.cacheDir,
                UUID.randomUUID().toString()
            )
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, imageCompressionQuality, fileOutputStream)
            fileOutputStream.close()
            return Uri.fromFile(file)
        }
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

    val callback = createOnBackPressedCallback(findNavController())

    override fun onDestroyView() {
        super.onDestroyView()
        destroyCallback(callback)
        _binding = null
    }

}