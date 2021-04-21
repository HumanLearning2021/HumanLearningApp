package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing.AddPictureContract.Companion.finishWith
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing.AddPictureContract.Companion.parseArgs
import java.io.File
import java.util.concurrent.Executors

/*
Activity where an administrator can take a picture to add it to a selected data set.
Should be started using the ActivityResultContract provided by the AddPictureContract object.
 */
class TakePictureActivity : AppCompatActivity() {

    private var categories = setOf<Category>()
    private lateinit var imageCapture: ImageCapture
    private lateinit var capturedImageUri: Uri
    private lateinit var chosenCategory: Category
    private var imageTaken: Boolean = false
    private var categorySet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkIntentExtras()

        if (cameraIsAvailable()) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    setupActivityLayout()
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
    }

    private fun checkIntentExtras() {
        val givenCategories = parseArgs()
        categories = categories.plus(givenCategories)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSave(view: View) {
        finishWith(chosenCategory, capturedImageUri)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onTakePicture(view: View) {
        val executor = Executors.newSingleThreadExecutor()
        val file: File = filesDir
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputFileOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Log.e(error.imageCaptureError.toString(), error.message.toString())
                    runOnUiThread {
                        showCaptureErrorDialog()
                    }

                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    runOnUiThread {
                        capturedImageUri = Uri.fromFile(file)
                        imageTaken = true
                        setCaptureButton()
                        notifySaveButton()
                    }
                }
            })
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSelectCategoryButton(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle(getString(R.string.AddPicture_categorySelectionDialogTitle))
            var catCopy = emptySet<Category>()
            catCopy = catCopy.plus(categories)
            setItems(catCopy.map { cat -> cat.name }.toTypedArray()) { _, category_index ->
                val button = findViewById<Button>(R.id.selectCategoryButton)
                chosenCategory = categories.elementAt(category_index)
                button.text = chosenCategory.name
                button.apply {
                    setBackgroundColor(getColor(R.color.button_set))
                    button.setTextColor(getColor(R.color.black))
                }
                categorySet = true
                notifySaveButton()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setupActivityLayout() {
        setContentView(R.layout.activity_camera)
        findViewById<Button>(R.id.selectCategoryButton).setOnClickListener(this::onSelectCategoryButton)
        findViewById<Button>(R.id.saveButton).setOnClickListener(this::onSave)
        findViewById<ImageView>(R.id.cameraImageView).isVisible = false
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            setupCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun setupCamera(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder().build()

        val cameraSelector: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        preview.setSurfaceProvider(findViewById<PreviewView>(R.id.cameraPreviewView).surfaceProvider)

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

        findViewById<Button>(R.id.takePictureButton).setOnClickListener(this::onTakePicture)
    }

    private fun setupRequestPermissionLauncher(): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                setupActivityLayout()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.AddPicture_permissionGrantedToast),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                permissionNeededDialog()
            }
        }
    }

    private fun permissionNeededDialog() {
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage(getString(R.string.AddPicture_permissionNeededDialogMessage))
            setTitle(getString(R.string.AddPicture_permissionNeededDialogTitle))
            setPositiveButton("OK") { _, _ ->
                super.onBackPressed()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun showCaptureErrorDialog() {
        val builder = AlertDialog.Builder(this)

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
        return applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun resetCaptureButton(view: View) {
        val button = findViewById<Button>(R.id.takePictureButton)
        updateButton(
            button,
            R.string.AddPicture_takePictureButtonText,
            R.color.white,
            R.color.button_default
        )
        findViewById<PreviewView>(R.id.cameraPreviewView).isVisible = true
        findViewById<ImageView>(R.id.cameraImageView).isVisible = false
        imageTaken = false
        notifySaveButton()
        button.setOnClickListener(this::onTakePicture)
    }

    private fun setCaptureButton() {
        val button = findViewById<Button>(R.id.takePictureButton)
        updateButton(
            button,
            R.string.AddPicture_takePictureButtonTextWhenImageTaken,
            R.color.black,
            R.color.button_set
        )
        findViewById<PreviewView>(R.id.cameraPreviewView).isVisible = false
        val imageView = findViewById<ImageView>(R.id.cameraImageView)
        imageView.isVisible = true
        imageView.setImageDrawable(Drawable.createFromPath(capturedImageUri.path))
        button.setOnClickListener(this::resetCaptureButton)
    }

    private fun updateButton(
        button: Button,
        text: Int,
        textColorCode: Int,
        backgroundColorCode: Int
    ) {
        button.apply {
            setBackgroundColor(getColor(backgroundColorCode))
            setTextColor(getColor(textColorCode))
            setText(text)
        }
    }

    private fun notifySaveButton() {
        findViewById<Button>(R.id.saveButton).isEnabled = categorySet && imageTaken
    }

    companion object {
        val Contract = AddPictureContract(TakePictureActivity::class.java)
    }
}
