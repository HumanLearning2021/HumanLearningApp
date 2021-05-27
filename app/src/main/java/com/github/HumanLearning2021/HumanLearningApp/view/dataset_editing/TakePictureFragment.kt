package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentTakePictureBinding
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

/**
 * Fragment used to take a picture with the camera which is then added to the dataset.
 */
class TakePictureFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity

    private val args: AddPictureFragmentArgs by navArgs()

    private var categories = setOf<Category>()
    private lateinit var imageCapture: ImageCapture
    private lateinit var pictureUri: Uri
    private lateinit var chosenCategory: Category
    private lateinit var datasetId: Id // ugly hack, but necessary to navigate back to display dataset fragment. Popping backstack doesnt seem to work
    private var imageTaken: Boolean = false
    private var categorySet: Boolean = false

    private var _binding: FragmentTakePictureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = requireActivity()

        datasetId = args.datasetId
        val givenCategories = args.categories.toList()
        categories = categories.plus(givenCategories)

        _binding = FragmentTakePictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        /**
         * Ask the permission to use the camera to the user if needed and launch the camera.
         */
        if (cameraIsAvailable()) {
            when {
                ContextCompat.checkSelfPermission(
                    parentActivity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    setupFragmentLayout()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    permissionNeededDialog()
                }
                else -> {
                    setupRequestPermissionLauncher().launch(Manifest.permission.CAMERA)
                }
            }
        } else {
            permissionNeededDialog()
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

    /**
     * Send the new picture to the display dataset fragment who adds the picture to the dataset.
     */
    @Suppress("UNUSED_PARAMETER")
    private fun onSave(view: View) {
        setFragmentResult(
            AddPictureFragment.REQUEST_KEY,
            bundleOf("chosenCategory" to chosenCategory, "pictureUri" to pictureUri)
        )
        findNavController().popBackStack()
    }

    /**
     * Save the picture taken and display it.
     */
    @Suppress("UNUSED_PARAMETER")
    private fun onTakePicture(view: View) {
        val executor = Executors.newSingleThreadExecutor()
        val outputStream = ByteArrayOutputStream()
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputStream).build()
        imageCapture.takePicture(
            outputFileOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Log.e(error.imageCaptureError.toString(), error.message.toString())
                    parentActivity.runOnUiThread {
                        showCaptureErrorDialog()
                    }

                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    parentActivity.runOnUiThread {
                        val bytes = outputStream.toByteArray()
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        pictureUri =
                            AddPictureFragment.applyImageSizeReduction(bitmap, requireContext())
                        imageTaken = true
                        setCaptureButton()
                        notifySaveButton()
                    }
                }
            })
    }

    /**
     * Allow the user to select the category of the picture.
     */
    @Suppress("UNUSED_PARAMETER")
    private fun onSelectCategoryButton(view: View) {
        val builder = AlertDialog.Builder(parentActivity)
        builder.apply {
            setTitle(getString(R.string.AddPicture_categorySelectionDialogTitle))
            var catCopy = emptySet<Category>()
            catCopy = catCopy.plus(categories)
            setItems(catCopy.map { cat -> cat.name }.toTypedArray()) { _, category_index ->
                val button = binding.selectCategoryButton
                chosenCategory = categories.elementAt(category_index)
                button.text = chosenCategory.name
                button.apply {
                    setBackgroundColor(getColor(parentActivity, R.color.button_set))
                    button.setTextColor(getColor(parentActivity, R.color.black))
                }
                categorySet = true
                notifySaveButton()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setupFragmentLayout() {
        binding.selectCategoryButton.setOnClickListener(this::onSelectCategoryButton)
        binding.saveButton.setOnClickListener(this::onSave)
        binding.cameraImageView.isVisible = false
        val cameraProviderFuture = ProcessCameraProvider.getInstance(parentActivity)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            setupCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(parentActivity))
    }

    private fun setupCamera(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder().build()

        val cameraSelector: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        preview.setSurfaceProvider(binding.cameraPreviewView.surfaceProvider)

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

        binding.takePictureButton.setOnClickListener(this::onTakePicture)
    }

    private fun setupRequestPermissionLauncher(): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                setupFragmentLayout()
                Toast.makeText(
                    parentActivity.applicationContext,
                    getString(R.string.AddPicture_permissionGrantedToast),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                permissionNeededDialog()
            }
        }
    }

    /**
     * Ask the permission to the user to use the camera.
     */
    private fun permissionNeededDialog() {

        val builder = AlertDialog.Builder(parentActivity)

        builder.apply {
            setMessage(getString(R.string.AddPicture_permissionNeededDialogMessage))
            setTitle(getString(R.string.AddPicture_permissionNeededDialogTitle))
            setPositiveButton("OK") { _, _ ->
                findNavController().popBackStack() //TODO: check
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun showCaptureErrorDialog() {
        val builder = AlertDialog.Builder(parentActivity)

        builder.apply {
            setMessage(R.string.AddPicture_errorWhileTakingPictureDialogMessage)
            setTitle(R.string.AddPicture_errorWhileTakingPictureDialogTitle)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun cameraIsAvailable(): Boolean {
        return parentActivity.applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun resetCaptureButton(view: View) {
        val button = binding.takePictureButton
        updateButton(
            button,
            R.string.AddPicture_takePictureButtonText,
            R.color.white,
            R.color.button_default
        )
        binding.cameraPreviewView.isVisible = true
        binding.cameraImageView.isVisible = false
        imageTaken = false
        notifySaveButton()
        button.setOnClickListener(this::onTakePicture)
    }

    private fun setCaptureButton() {
        val button = binding.takePictureButton
        updateButton(
            button,
            R.string.AddPicture_takePictureButtonTextWhenImageTaken,
            R.color.black,
            R.color.button_set
        )
        binding.cameraPreviewView.isVisible = false
        val imageView = binding.cameraImageView
        imageView.isVisible = true
        imageView.setImageDrawable(Drawable.createFromPath(pictureUri.path))
        button.setOnClickListener(this::resetCaptureButton)
    }

    private fun updateButton(
        button: Button,
        text: Int,
        textColorCode: Int,
        backgroundColorCode: Int
    ) {
        button.apply {
            setBackgroundColor(getColor(parentActivity, backgroundColorCode))
            setTextColor(getColor(parentActivity, textColorCode))
            setText(text)
        }
    }

    private fun notifySaveButton() {
        /**
         * Can only save the picture if the user took a picture and selected a category.
         */
        binding.saveButton.isEnabled = categorySet && imageTaken
    }
}