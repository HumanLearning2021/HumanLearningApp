package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentDisplayImageBinding
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentLearningBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningFragmentArgs
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
    private lateinit var datasetId: Id
    private lateinit var category: Category

    private val args: DisplayImageFragmentArgs by navArgs()
    private var _binding: FragmentDisplayImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        _binding = FragmentDisplayImageBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        picture = args.picture
        datasetId = args.datasetId
        category = picture!!.category
        parentActivity.findViewById<TextView>(R.id.display_image_viewCategory).text = category.name
        picture!!.displayOn(
            parentActivity,
            parentActivity.findViewById(R.id.display_image_viewImage)
        )

        parentActivity.findViewById<ImageButton>(R.id.display_image_delete_button)
            .setOnClickListener {
                removePicture()
            }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    private fun removePicture() {
        var noMorePicturesInThisCategory = false
        lifecycleScope.launch {
            dbManagement.removePicture(picture!!)
            Toast.makeText(
                parentActivity,
                getText(R.string.picturehasbeenremoved),
                Toast.LENGTH_SHORT
            ).show()

            if (dbManagement.getAllPictures(category).isEmpty()) {
                noMorePicturesInThisCategory = true
            }

            if (noMorePicturesInThisCategory) {
                val action =
                    DisplayImageFragmentDirections.actionDisplayImageFragmentToDisplayDatasetFragment(
                        datasetId
                    )
                findNavController().navigate(action)
            } else {
                val action =
                    DisplayImageFragmentDirections.actionDisplayImageFragmentToDisplayImageSetFragment(
                        datasetId,
                        category
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun setRepresentativePicture() {
        lifecycleScope.launch {
            //TODO: Set the picture as representative picture of the category
        }
    }
}