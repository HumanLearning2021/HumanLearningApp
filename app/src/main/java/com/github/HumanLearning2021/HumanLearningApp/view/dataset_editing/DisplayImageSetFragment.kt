package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentDisplayImageSetBinding
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentLearningBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.DummyDatabase
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
class DisplayImageSetFragment: Fragment() {
    private lateinit var parentActivity: FragmentActivity

    @Inject
    @Demo2Database
    lateinit var dBManagement: DatabaseManagement

    private var categorizedPicturesList = setOf<CategorizedPicture>()
    private lateinit var datasetId: Id
    private lateinit var category: Category

    private val args: DisplayImageSetFragmentArgs by navArgs()
    private var _binding: FragmentDisplayImageSetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        _binding = FragmentDisplayImageSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        category = args.category
        datasetId = args.datasetId

        lifecycleScope.launch {
            categorizedPicturesList = dBManagement.getAllPictures(category)
            binding.displayImageSetName.text =
                category.name
            if (categorizedPicturesList.isNotEmpty()) {
                val displayImageSetAdapter =
                    DisplayImageSetAdapter(
                        categorizedPicturesList,
                        parentActivity
                    )

                parentActivity.findViewById<GridView>(R.id.display_image_set_imagesGridView).adapter =
                    displayImageSetAdapter
                setPictureItemListener()
            }
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }



    private class DisplayImageSetAdapter(
        private val images: Set<CategorizedPicture>,
        private val context: Activity
    ) : BaseAdapter() {

        private var layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view0: View?, viewGroup: ViewGroup?): View {
            val view =
                view0 ?: layoutInflater.inflate(R.layout.image_item, viewGroup!!, false)

            val imageView = view.findViewById<ImageView>(R.id.image_item_imageView)

            images.elementAt(position).displayOn(context, imageView as ImageView)

            return view
        }

        override fun getItem(position: Int): Any {
            return images.elementAt(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return images.size
        }

    }

    private fun setPictureItemListener(){
        binding.displayImageSetImagesGridView.setOnItemClickListener{_, _, i, _ ->
            val action = DisplayImageSetFragmentDirections.actionDisplayImageSetFragmentToDisplayImageFragment(categorizedPicturesList.elementAt(i), datasetId)
        }
    }
}
