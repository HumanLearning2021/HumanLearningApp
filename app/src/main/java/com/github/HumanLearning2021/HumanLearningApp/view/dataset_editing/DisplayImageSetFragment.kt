package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentDisplayImageSetBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.FragmentOptionsUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Fragment to display all the pictures of a category.
 *
 * The name of the category and all the pictures of the category are displayed.
 *
 * Possible actions :
 * - Get the information of what the user can do by clicking on the info menu button.
 * - Can delete one or multiple pictures with a long click on one picture and then by clicking
 * on the trashcan icon (Possibility to select multiple pictures after the long click).
 * - Can set a picture as representative of the category with a long click and then by clicking
 * on the star icon (this action deletes the picture from the pictures of the category).
 */
@AndroidEntryPoint
class DisplayImageSetFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @Inject
    @ProductionDatabaseName
    lateinit var dbName: String

    lateinit var dBManagement: DatabaseManagement

    @Inject
    lateinit var imageDisplayer: ImageDisplayer

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
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        category = args.category
        datasetId = args.datasetId

        /**
         * Retrieve all the pictures of the category and display them.
         */
        lifecycleScope.launch {
            categorizedPicturesList = dBManagement.getAllPictures(category)
            binding.displayImageSetName.text =
                category.name
            if (categorizedPicturesList.isNotEmpty()) {
                displayImageSetAdapter =
                    DisplayImageSetAdapter(
                        categorizedPicturesList,
                    )

                parentActivity.findViewById<GridView>(R.id.display_image_set_imagesGridView).adapter =
                    displayImageSetAdapter
                setPictureItemListener()
            }
        }
        binding.displayImageSetImagesGridView.choiceMode = GridView.CHOICE_MODE_MULTIPLE_MODAL
        setGridViewMultipleChoiceModeListener()
    }
    
    override fun onDestroyView() {
        binding.displayImageSetImagesGridView.choiceMode = GridView.CHOICE_MODE_MULTIPLE
        super.onDestroyView()
        _binding = null
    }

    /**
     * Adapter of the grid displaying pictures of the category.
     *
     * @param pictures the pictures of the category
     * @constructor Creates an adapter with the given pictures.
     */
    private inner class DisplayImageSetAdapter(
        pictures: Set<CategorizedPicture>,
    ) : BaseAdapter() {

        var adapterPictures = pictures

        private var layoutInflater =
            parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view0: View?, viewGroup: ViewGroup?): View {
            val view =
                view0 ?: layoutInflater.inflate(R.layout.image_item, viewGroup!!, false)

            val imageView = view.findViewById<ImageView>(R.id.image_item_imageView)

            with(imageDisplayer) {
                lifecycleScope.launch {
                    adapterPictures.elementAt(position).displayOn(imageView as ImageView)
                }
            }

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

        /**
         * updates the adapter and display the new pictures
         *
         * @param newPictures the new pictures to be displayed.
         */
        fun updatePictures(newPictures: Set<CategorizedPicture>) {
            adapterPictures = newPictures
            notifyDataSetChanged()
        }
    }

    /**
     * Listener to navigate to the display image fragment when a picture is clicked.
     */
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

    /**
     * Listener for the multiple selection of the pictures after a long click.
     */
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
                    /**
                     * delete all the selected pictures.
                     */
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
                    /**
                     * Set the selected picture as representative of the category.
                     */
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
                /**
                 * When a picture is selected, lower it's alpha value to clearly see that the image is selected.
                 */
                if (checked) {
                    numberOfSelectedPictures += 1
                    mode!!.title =
                        resources.getQuantityString(
                            R.plurals.numberOfSelectedPicturesText,
                            numberOfSelectedPictures,
                            numberOfSelectedPictures
                        )
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
                        resources.getQuantityString(
                            R.plurals.numberOfSelectedPicturesText,
                            numberOfSelectedPictures,
                            numberOfSelectedPictures
                        )
                    categorizedPicturesSelectedList =
                        categorizedPicturesSelectedList.minus(
                            categorizedPicturesList.elementAt(
                                position
                            )
                        )
                }

                /**
                 * The star icon to set as representative picture is only displayed when exactly one
                 * picture is selected.
                 */
                mode.menu[1].isVisible = numberOfSelectedPictures == 1
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.display_imageset_info_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return FragmentOptionsUtil.displayInfoMenu(
            item = item,
            infoItemId = R.id.display_imageset_menu_info,
            title = getString(R.string.info),
            message = getString(R.string.displayImagesetInfo),
            context = this.context
        )
    }
}
