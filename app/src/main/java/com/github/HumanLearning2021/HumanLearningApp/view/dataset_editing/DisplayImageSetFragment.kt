package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentDisplayImageSetBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DisplayImageSetFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity

    @Inject
    @Demo2Database
    lateinit var dBManagement: DatabaseManagement

    private var categorizedPicturesList = setOf<CategorizedPicture>()
    private var categorizedPicturesSelectedList = setOf<CategorizedPicture>()
    private var numberOfSelectedPictures = 0
    private lateinit var datasetId: Id
    private lateinit var category: Category
    private lateinit var displayImageSetAdapter: DisplayImageSetAdapter

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
                displayImageSetAdapter =
                    DisplayImageSetAdapter(
                        categorizedPicturesList,
                        parentActivity
                    )

                parentActivity.findViewById<GridView>(R.id.display_image_set_imagesGridView).adapter =
                    displayImageSetAdapter
                setPictureItemListener()
            }
        }
        binding.displayImageSetImagesGridView.choiceMode = GridView.CHOICE_MODE_MULTIPLE_MODAL
        setGridViewMultipleChoiceModeListener()
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


    private class DisplayImageSetAdapter(
        pictures: Set<CategorizedPicture>,
        private val context: Activity
    ) : BaseAdapter() {

        var adapterPictures = pictures

        private var layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view0: View?, viewGroup: ViewGroup?): View {
            val view =
                view0 ?: layoutInflater.inflate(R.layout.image_item, viewGroup!!, false)

            val imageView = view.findViewById<ImageView>(R.id.image_item_imageView)

            adapterPictures.elementAt(position).displayOn(context, imageView as ImageView)

            return view
        }

        override fun getItem(position: Int): Any {
            return adapterPictures.elementAt(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return adapterPictures.size
        }

        fun updatePictures(newPictures: Set<CategorizedPicture>) {
            adapterPictures = newPictures
            notifyDataSetChanged()
        }
    }

    private fun setPictureItemListener() {
        binding.displayImageSetImagesGridView.setOnItemClickListener { _, _, i, _ ->
            val action =
                DisplayImageSetFragmentDirections.actionDisplayImageSetFragmentToDisplayImageFragment(
                    categorizedPicturesList.elementAt(i),
                    datasetId
                )
            findNavController().navigate(action)
        }
    }

    private fun setGridViewMultipleChoiceModeListener() {
        binding.displayImageSetImagesGridView.setMultiChoiceModeListener(object :
            AbsListView.MultiChoiceModeListener {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                val inflater = mode!!.menuInflater
                inflater!!.inflate(R.menu.display_imageset_menu, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when (item!!.itemId) {
                    R.id.delete_pictures ->
                        lifecycleScope.launch {
                            for (pic in categorizedPicturesSelectedList) {
                                categorizedPicturesList = categorizedPicturesList.minus(pic)
                                dBManagement.removePicture(pic)
                            }
                            numberOfSelectedPictures = 0
                            mode!!.finish()
                        }
                    R.id.set_representative_picture ->
                        if (numberOfSelectedPictures == 1) {
                            //TODO: SET REPRESENTATIVE PICTURE
                        } else {
                            Toast.makeText(
                                context,
                                "To much pictures selected. Please select only one to set as representative picture of the category",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    else -> {

                    }
                }
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                for (i in categorizedPicturesList.indices) {
                    binding.displayImageSetImagesGridView[i].alpha = 1F
                }
                displayImageSetAdapter.updatePictures(categorizedPicturesList)
                displayImageSetAdapter.notifyDataSetChanged()
            }

            override fun onItemCheckedStateChanged(
                mode: ActionMode?,
                position: Int,
                id: Long,
                checked: Boolean
            ) {
                if (checked) {
                    numberOfSelectedPictures += 1
                    mode!!.title = "$numberOfSelectedPictures selected pictures"
                    binding.displayImageSetImagesGridView[position].alpha = 0.5F
                    categorizedPicturesSelectedList =
                        categorizedPicturesSelectedList.plus(
                            categorizedPicturesList.elementAt(
                                position
                            )
                        )
                } else {
                    numberOfSelectedPictures -= 1
                    binding.displayImageSetImagesGridView[position].alpha = 1F
                    mode!!.title = "$numberOfSelectedPictures selected pictures"
                    categorizedPicturesSelectedList =
                        categorizedPicturesSelectedList.minus(
                            categorizedPicturesList.elementAt(
                                position
                            )
                        )
                }
            }

        })
    }
}
