package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.github.HumanLearning2021.HumanLearningApp.model.Category

/**
 * A contract template for activities that take a list of categories for the
 * user to select from, and return a Pair containing the selected category and
 * the Uri pointing to the image
 */
class AddPictureContract<T : Activity>(private val cls: Class<T>) :
    ActivityResultContract<ArrayList<Category>, Pair<Category, Uri>?>() {
    override fun createIntent(context: Context, input: ArrayList<Category>?): Intent =
        Intent(context, cls).putParcelableArrayListExtra(
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

    companion object {
        @Suppress("UNCHECKED_CAST")  // due to type erasure
        fun Activity.parseArgs() =
            intent.extras!!["categories"] as ArrayList<Category>

        fun Activity.finishWith(category: Category, image: Uri) {
            val returnIntent = Intent()
            val bundle = Bundle().apply {
                putParcelable("category", category)
                putParcelable("image", image)
            }
            returnIntent.putExtra("result", bundle)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }
}
