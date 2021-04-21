package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DisplayImageFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity

    @Inject
    @Demo2Database
    lateinit var dbManagement: DatabaseManagement

    private var picture: CategorizedPicture? = null
    private lateinit var datasetId: String
    private lateinit var category: Category

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        return inflater.inflate(R.layout.fragment_display_image, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        picture =
            arguments?.getParcelable(ARG_PIC)
        datasetId = arguments?.getString(ARG_DSET_ID)!!
        if (picture != null) {
            category = picture!!.category
            parentActivity.findViewById<TextView>(R.id.display_image_viewCategory).text = category.name
            picture!!.displayOn(parentActivity, parentActivity.findViewById(R.id.display_image_viewImage))

            parentActivity.findViewById<ImageButton>(R.id.display_image_delete_button).setOnClickListener {
                removePicture()
            }
        }
    }

    private fun removePicture() {
        var noMorePicturesInThisCategory = false
        lifecycleScope.launch {
            if (dbManagement.getAllPictures(category).isEmpty()) {
                noMorePicturesInThisCategory = true
            }
            dbManagement.removePicture(picture!!)
            Toast.makeText(
                parentActivity,
                getText(R.string.picturehasbeenremoved),
                Toast.LENGTH_SHORT
            )
                .show()
            if (noMorePicturesInThisCategory) {
                launchDisplayDatasetActivity()
            } else {
                launchDisplayImageSetActivity()
            }
        }
    }

    private fun launchDisplayDatasetActivity() {
        startActivity(
            Intent(parentActivity, DisplayDatasetActivity::class.java)
            .putExtra("dataset_id", datasetId))
    }

    private fun launchDisplayImageSetActivity() {
        val intent = Intent(parentActivity, DisplayImageSetActivity::class.java)
        intent.putExtra(
            "category_of_pictures",
            category
        )
        intent.putExtra("dataset_id", datasetId)
        startActivity(intent)
    }

    companion object {
        const val ARG_PIC = "single_picture"
        const val ARG_DSET_ID = "dataset_id"

        fun newInstance(dSetId: String, picture: Parcelable): DisplayImageFragment {
            val args = Bundle()
            args.putString(ARG_DSET_ID, dSetId)
            args.putParcelable(ARG_PIC, picture)
            val fragment = DisplayImageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}