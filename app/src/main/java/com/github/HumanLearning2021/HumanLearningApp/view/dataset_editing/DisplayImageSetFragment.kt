package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentDisplayImageSetBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.GlobalDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class DisplayImageSetFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity

    @Inject
    @GlobalDatabaseManagement
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @Inject
    @ProductionDatabaseName
    lateinit var dbName: String

    lateinit var dBManagement: DatabaseManagement

    private var categorizedPicturesList = setOf<CategorizedPicture>()
    private var categorizedPicturesSelectedList = setOf<CategorizedPicture>()
    private var numberOfSelectedPictures = 0
    private lateinit var displayImageSetAdapter: DisplayImageSetAdapter
    private lateinit var datasetId: Id
    private lateinit var category: Category

    private val args: DisplayImageSetFragmentArgs by navArgs()
    private var _binding: FragmentDisplayImageSetBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            dBManagement = globalDatabaseManagement.accessDatabase(
                dbName
            )
        }
    }

    override fun onResume() {
        super.onResume()
        runBlocking {
            dBManagement = globalDatabaseManagement.accessDatabase(
                dbName
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

            val tenDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f,
                this@DisplayImageSetFragment.resources.displayMetrics
            )

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
                            categorizedPicturesSelectedList = emptySet()
                            numberOfSelectedPictures = 0
                            mode!!.finish()
                        }
                    R.id.set_representative_picture ->
                        lifecycleScope.launch {
                            val pic = categorizedPicturesSelectedList.first()
                            dBManagement.putRepresentativePicture(
                                pic
                            )
                            categorizedPicturesList = categorizedPicturesList.minus(pic)
                            categorizedPicturesSelectedList = emptySet()
                            numberOfSelectedPictures = 0
                            mode!!.finish()
                        }
                    else -> {

                    }
                }
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                for (i in categorizedPicturesList.indices) {
                    binding.displayImageSetImagesGridView[i].alpha = 1F
                    binding.displayImageSetImagesGridView[i].elevation = tenDp
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
                    mode!!.title =
                        getString(R.string.numberOfSelectedPicturesText, numberOfSelectedPictures)
                    binding.displayImageSetImagesGridView[position].alpha = 0.35F
                    binding.displayImageSetImagesGridView[position].elevation = 0F
                    categorizedPicturesSelectedList =
                        categorizedPicturesSelectedList.plus(
                            categorizedPicturesList.elementAt(
                                position
                            )
                        )
                } else {
                    numberOfSelectedPictures -= 1
                    binding.displayImageSetImagesGridView[position].alpha = 1F
                    binding.displayImageSetImagesGridView[position].elevation = tenDp
                    mode!!.title =
                        getString(R.string.numberOfSelectedPicturesText, numberOfSelectedPictures)
                    categorizedPicturesSelectedList =
                        categorizedPicturesSelectedList.minus(
                            categorizedPicturesList.elementAt(
                                position
                            )
                        )
                }

                mode.menu[1].isVisible = numberOfSelectedPictures == 1
            }

        })
    }
}
