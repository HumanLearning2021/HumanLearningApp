package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.Category

class SelectPictureActivity : AppCompatActivity() {
    private var selectedPicture: Uri? = null
    private var selectedCategory: Category? = null
    private lateinit var categories: List<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_picture)
        categories = parseArgs(intent.extras!!)

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

    private fun finishWith(category: Category, image: Uri) {
        val returnIntent = Intent()
        val bundle = Bundle().apply {
            putParcelable("category", category)
            putParcelable("image", image)
        }
        returnIntent.putExtra("result", bundle)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    /**
    The ActivityResultContract which should be used when launching this activity.
    The launch argument is a list containing the categories to select from.
    The return value is a Pair containing the selected category as a first element and the Uri pointing to the image as a second element
     */
    object SelectPictureContract :
        ActivityResultContract<ArrayList<Category>, Pair<Category, Uri>?>() {
        override fun createIntent(context: Context, input: ArrayList<Category>?): Intent =
            Intent(context, SelectPictureActivity::class.java).putParcelableArrayListExtra(
                "categories",
                input
            )

        override fun parseResult(resultCode: Int, result: Intent?): Pair<Category, Uri>? {
            return if (resultCode != Activity.RESULT_OK) {
                null
            } else {
                val bundle = result!!.extras!!.get("result") as Bundle
                Pair(bundle["category"] as Category, bundle["image"] as Uri)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")  // due to type erasure
    private fun parseArgs(extras: Bundle) =
        extras["categories"] as ArrayList<Category>

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
        findViewById<Button>(R.id.saveButton3).isEnabled = selectedCategory != null && selectedPicture != null
    }

    companion object {
        val RC_OPEN_PICTURE = "open picture".hashCode()
    }
}