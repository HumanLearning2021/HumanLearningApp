package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.Category

class AddPictureActivity : AppCompatActivity() {
    private lateinit var categories: ArrayList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_picture)
        categories = parseArgs(intent.extras!!)
        findViewById<Button>(R.id.use_camera).setOnClickListener {
            launchTakePictureActivity()
        }
        findViewById<Button>(R.id.select_existing_picture).setOnClickListener {
            launchSelectPictureActivity()
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

    private val selectPictureRegistration =
        registerForActivityResult(SelectPictureActivity.SelectPictureContract) {
            it?.let {
                finishWith(it.first, it.second)
            }
        }
    private val takePictureRegistration =
        registerForActivityResult(TakePictureActivity.TakePictureContract) {
            it?.let {
                finishWith(it.first, it.second)
            }
        }

    private fun launchSelectPictureActivity() {
        selectPictureRegistration.launch(categories)
    }

    private fun launchTakePictureActivity() {
        takePictureRegistration.launch(categories)
    }

    /**
    The ActivityResultContract which should be used when launching this activity.
    The launch argument is a list containing the categories to select from.
    The return value is a Pair containing the selected category as a first element and the Uri pointing to the image as a second element
     */
    object AddPictureContract :
        ActivityResultContract<ArrayList<Category>, Pair<Category, Uri>?>() {
        override fun createIntent(context: Context, input: ArrayList<Category>?): Intent =
            Intent(context, AddPictureActivity::class.java).putParcelableArrayListExtra(
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
}