package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing.AddPictureContract.Companion.finishWith
import com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing.AddPictureContract.Companion.parseArgs

class SelectPictureActivity : AppCompatActivity() {
    private var selectedPicture: Uri? = null
    private var selectedCategory: Category? = null
    private lateinit var categories: List<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_picture)
        categories = parseArgs()

        findViewById<Button>(R.id.choosePictureButton).setOnClickListener {
            launchOpenPicture()
        }
        findViewById<Button>(R.id.selectCategoryButton2).setOnClickListener {
            onSelectCategoryButton()
        }

        findViewById<Button>(R.id.saveButton3).setOnClickListener {
            finishWith(selectedCategory!!, selectedPicture!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")  // FIXME: use something non-deprecated
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_OPEN_PICTURE -> {
                data?.data?.also {
                    selectedPicture = it
                    displayPicture(it)
                    notifySaveButton()
                }
            }
        }
    }

    private fun displayPicture(pic: Uri) {
        Glide.with(this).load(pic).into(findViewById(R.id.selectedPicturePreview))
    }

    private fun launchOpenPicture() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"

        }
        @Suppress("DEPRECATION")  // FIXME: use something non-deprecated
        startActivityForResult(intent, RC_OPEN_PICTURE)
    }

    private fun onSelectCategoryButton() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle(getString(R.string.AddPicture_categorySelectionDialogTitle))
            setItems(categories.map { cat -> cat.name }.toTypedArray()) { _, category_index ->
                val button = findViewById<Button>(R.id.selectCategoryButton2)
                categories.elementAt(category_index).let {
                    button.text = it.name
                    selectedCategory = it
                    button.apply {
                        setBackgroundColor(getColor(R.color.button_set))
                        button.setTextColor(getColor(R.color.black))
                    }
                    notifySaveButton()
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
    }


    private fun notifySaveButton() {
        findViewById<Button>(R.id.saveButton3).isEnabled =
            selectedCategory != null && selectedPicture != null
    }

    companion object {
        val RC_OPEN_PICTURE = "open picture".hashCode()
        val Contract = AddPictureContract(SelectPictureActivity::class.java)

    }
}