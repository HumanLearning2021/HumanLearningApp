package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentDisplayDatasetBinding
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentLearningBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.DummyDatabase
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.view.learning.LearningFragmentArgs
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
    private lateinit var datasetId: String
    private lateinit var dataset: Dataset

    private var _binding: FragmentDisplayDatasetBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        _binding = FragmentDisplayDatasetBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datasetId = args.datasetId!!

        // Ugly hack: if these arguments aren't null, it means the previous fragment was addPicture
        // and it saved a picture
        if (args.chosenCategory != null && args.pictureUri != null) {
            lifecycleScope.launch {
                dbManagement.putPicture(args.pictureUri!!, args.chosenCategory!!)
            }
        }

        lifecycleScope.launch {
            dataset = dbManagement.getDatasetById(datasetId)!!
            var representativePictures = setOf<CategorizedPicture>()
            binding.displayDatasetName.setText(dataset.name)
            categories = dataset.categories
            for (cat in categories) {
                val pictures = dbManagement.getAllPictures(cat)
                if (pictures.isNotEmpty()) {
                    val reprPicture = dbManagement.getRepresentativePicture(cat.id)
                    representativePictures = if (reprPicture == null) {
                        representativePictures.plus(dbManagement.getPicture(pictures.first().id)!!)
                    } else {
                        representativePictures.plus(reprPicture)
                    }
                }
            }

            val displayDatasetAdapter =
                DisplayDatasetAdapter(
                    representativePictures,
                    parentActivity
                )

            binding.displayDatasetImagesGridView.adapter = displayDatasetAdapter

            setGridViewItemListener()
            setTextChangeListener()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.display_dataset_menu_modify_categories -> {
                val action =
                    DisplayDatasetFragmentDirections.actionDisplayDatasetFragmentToCategoriesEditingFragment(
                        datasetId
                    )
                findNavController().navigate(action)
                true
            }
            //Clicked on Add new Picture button
            else -> {
                val action =
                    DisplayDatasetFragmentDirections.actionDisplayDatasetFragmentToAddPictureFragment(
                        categories.toTypedArray(),
                        datasetId
                    )
                findNavController().navigate(action)
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.display_dataset_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private class DisplayDatasetAdapter(
        private val images: Set<CategorizedPicture>,
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

            imageCat?.text = images.elementAt(position).category.name
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

    private fun setGridViewItemListener() {
        binding.displayDatasetImagesGridView
            .setOnItemClickListener { adapterView, view, i, l ->
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