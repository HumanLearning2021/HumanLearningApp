package com.github.HumanLearning2021.HumanLearningApp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture

/*
Activity where an administrator can take a picture to add it to a selected data set
 */
class AddPictureActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private val dummyCategories = arrayOf(
        "category1",
        "category2",
        "category3",
        "category4",
        "category5",
        "category6",
        "category7",
        "category8",
        "category9",
        "category10"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    private fun cameraIsAvailable(): Boolean {
        return applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
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

    private fun onSelectCategoryButton(v: View) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle(getString(R.string.AddPicture_categorySelectionDialogTitle))
            setItems(dummyCategories) { dialog, category_index ->
                val button = findViewById<Button>(R.id.selectCategoryButton)
                button.text = dummyCategories[category_index]
                button.setBackgroundColor(getColor(R.color.button_set))
                button.setTextColor(getColor(R.color.black))
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setupActivityLayout() {
        setContentView(R.layout.activity_camera)
        findViewById<Button>(R.id.selectCategoryButton).setOnClickListener(this::onSelectCategoryButton)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        val cameraSelector: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        preview.setSurfaceProvider(findViewById<PreviewView>(R.id.cameraPreviewView).surfaceProvider)

        val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview)
    }

    private fun permissionNeededDialog() {
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage(getString(R.string.AddPicture_permissionNeededDialogMessage))
            setTitle(getString(R.string.AddPicture_permissionNeededDialogTitle))
            setPositiveButton("OK") { dialog, which ->
                super.onBackPressed()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}