package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentDisplayImageBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Fragment used to display a single image with the name of it's category.
 */
@AndroidEntryPoint
class DisplayImageFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @Inject
    @ProductionDatabaseName
    lateinit var dbName: String

    lateinit var dbManagement: DatabaseManagement

    @Inject
    lateinit var imageDisplayer: ImageDisplayer

    private var picture: CategorizedPicture? = null
    private lateinit var datasetId: Id
    private lateinit var category: Category

    private val args: DisplayImageFragmentArgs by navArgs()
    private var _binding: FragmentDisplayImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            dbManagement = globalDatabaseManagement.accessDatabase(
                dbName
            )
        }
    }

    override fun onResume() {
        super.onResume()
        runBlocking {
            dbManagement = globalDatabaseManagement.accessDatabase(
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
        _binding = FragmentDisplayImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        picture = args.picture
        datasetId = args.datasetId
        category = picture!!.category
        parentActivity.findViewById<TextView>(R.id.textView_display_image).text = category.name
        with(imageDisplayer) {
            lifecycleScope.launch {
                picture!!.displayOn(parentActivity.findViewById(R.id.imageView_display_image))
            }
        }

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
}
