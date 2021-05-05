package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentDisplayDatasetBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DisplayDatasetFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity

    @Inject
    @Demo2Database
    lateinit var dbManagement: DatabaseManagement

    private val args: DisplayDatasetFragmentArgs by navArgs()

    private lateinit var categories: Set<Category>
    private lateinit var datasetId: Id
    private lateinit var dataset: Dataset

    private var _binding: FragmentDisplayDatasetBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val pictureUri = bundle.getParcelable<Uri>("pictureUri")
            val chosenCategory = bundle.getParcelable<Category>("chosenCategory")
            lifecycleScope.launch {
                dbManagement.putPicture(pictureUri!!, chosenCategory!!)
            }
        }
    }

    companion object {
        const val REQUEST_KEY = "DisplayDatasetFragmentRequestKey"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = requireActivity()
        _binding = FragmentDisplayDatasetBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datasetId = args.datasetId!!

        val representativePictures = ArrayList<Any>()

        lifecycleScope.launch {
            dataset = dbManagement.getDatasetById(datasetId)!!
            binding.displayDatasetName.setText(dataset.name)
            categories = dataset.categories

            for (cat in categories) {
                val reprPicture = dbManagement.getRepresentativePicture(cat.id)
                if (reprPicture == null) {
                    representativePictures.add(0)
                } else {
                    representativePictures.add(reprPicture)
                }
            }

            val displayDatasetAdapter =
                DisplayDatasetAdapter(
                    representativePictures,
                    categories,
                    parentActivity
                )

            binding.displayDatasetImagesGridView.adapter = displayDatasetAdapter

            setGridViewItemListener()
            setTextChangeListener()
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.display_dataset_menu_modify_categories -> {
                findNavController().navigate(
                    DisplayDatasetFragmentDirections.actionDisplayDatasetFragmentToCategoriesEditingFragment(
                        datasetId
                    )
                )
                true
            }
            R.id.display_dataset_menu_delete_dataset -> {
                AlertDialog.Builder(this.context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Deleting dataset")
                    .setMessage("Are you sure you want to delete this dataset?")
                    .setPositiveButton("Yes"
                    ) { _, _ ->
                        lifecycleScope.launch {
                            dbManagement.deleteDataset(datasetId)
                            findNavController().popBackStack()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
                true
            }
            //Clicked on Add new Picture button
            else -> {
                findNavController().navigate(
                    DisplayDatasetFragmentDirections.actionDisplayDatasetFragmentToAddPictureFragment(
                        categories.toTypedArray(),
                        datasetId
                    )
                )
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.display_dataset_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    val callback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback.isEnabled = false
        callback.remove()
    }

    private class DisplayDatasetAdapter(
        private val images: ArrayList<Any>,
        private val categories: Set<Category>,

        private val context: Activity
    ) : BaseAdapter() {

        private var layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view0: View?, viewGroup: ViewGroup?): View {
            val view =
                view0 ?: layoutInflater.inflate(
                    R.layout.image_and_category_item,
                    viewGroup!!,
                    false
                )

            val imageCat = view.findViewById<TextView>(R.id.image_and_category_item_imageCategory)
            val imageView = view.findViewById<ImageView>(R.id.image_and_category_item_imageView)

            val picture = images.elementAt(position)
            if(picture is CategorizedPicture) {
                imageCat?.text = picture.category.name
                picture.displayOn(
                    context,
                    imageView as ImageView
                )
            } else {
                imageCat?.text = categories.elementAt(position).name
                imageView.setImageResource(R.drawable.default_representative_picture)
            }

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

    private fun setGridViewItemListener() {
        binding.displayDatasetImagesGridView
            .setOnItemClickListener { _, _, i, _ ->
                val category = categories.elementAt(i)
                val action =
                    DisplayDatasetFragmentDirections.actionDisplayDatasetFragmentToDisplayImageSetFragment(
                        datasetId,
                        category
                    )
                findNavController().navigate(action)
            }
    }

    private fun setTextChangeListener() {
        binding.displayDatasetName.doAfterTextChanged {
            lifecycleScope.launch {
                dataset = dbManagement.editDatasetName(
                    dataset,
                    binding.displayDatasetName.text.toString()
                )
            }
        }
    }


}