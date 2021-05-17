package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentLearningBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.GlobalDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import com.github.HumanLearning2021.HumanLearningApp.presenter.LearningPresenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class LearningFragment : Fragment() {
    private lateinit var audioFeedback: LearningAudioFeedback
    private lateinit var datasetId: Id
    private lateinit var dataset: Dataset
    private val args: LearningFragmentArgs by navArgs()
    private var _binding: FragmentLearningBinding? = null
    private val binding get() = _binding!!

    /**
     * This stores the image views on which the representatives of the target categories are displayed
     */
    private lateinit var targetImageViews: List<ImageView>

    lateinit var learningPresenter: LearningPresenter

    @Inject
    lateinit var authPresenter: AuthenticationPresenter

    @Inject
    @GlobalDatabaseManagement
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    lateinit var dbMgt: DatabaseManagement

    private lateinit var parentActivity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            dbMgt = globalDatabaseManagement.accessDatabase(
                getString(
                    R.string.production_database_name
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = requireActivity()
        audioFeedback = LearningAudioFeedback(parentActivity.applicationContext)
        _binding = FragmentLearningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        datasetId = args.datasetId
        lifecycleScope.launch {
            dataset = dbMgt.getDatasetById(datasetId)!!
            targetImageViews = adaptDisplayToNumberOfCategories(dataset)

            learningPresenter = LearningPresenter(dbMgt, args.learningMode, dataset, authPresenter)
            learningPresenter.updateForNextSorting(
                parentActivity,
                targetImageViews,
                binding.learningToSort
            )
            // sets the listeners for the image views of the sorting
            setEventListeners()
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    /**
     * This function adapts the display to the number of categories in the dataset
     * For example, if the dataset only has 2 categories, one of the categories will not be displayed
     * @param dataset dataset that is used for the learning
     * @return the ImageViews that stay displayed on screen
     */
    private fun adaptDisplayToNumberOfCategories(dataset: Dataset): List<ImageView> {
        val nbCategories = dataset.categories.size
        require(nbCategories > 0) {
            "A dataset used for learning should have at least one category"
        }
        val makeInvisible = { v: List<View> -> v.forEach { it.visibility = View.INVISIBLE } }
        return with(binding) {
            when (nbCategories) {
                1 -> {
                    makeInvisible(listOf(learningCat0, learningCat2))
                    listOf(learningCat1)
                }
                2 -> {
                    makeInvisible(listOf(learningCat2))
                    listOf(learningCat0, learningCat1)
                }
                else -> listOf(learningCat0, learningCat1, learningCat2)
            }
        }
    }

    /**
     * Sets the event listeners for the image to sort and the target image views
     */
    private fun setEventListeners() = with(binding) {
        learningToSort.setOnTouchListener { e, v -> onImageToSortTouched(e, v) }
        learningCat0.setOnDragListener(targetOnDragListener)
        learningCat1.setOnDragListener(targetOnDragListener)
        learningCat2.setOnDragListener(targetOnDragListener)
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

    override fun onResume() {
        super.onResume()
        runBlocking {
            dbMgt = globalDatabaseManagement.accessDatabase(
                getString(
                    R.string.production_database_name
                )
            )
        }
        audioFeedback.initMediaPlayers()
    }

    override fun onPause() {
        super.onPause()
        audioFeedback.releaseMediaPlayers()
    }


    private val opaque = 1.0f
    private val halfOpaque = opaque / 2

    /**
     * This is the listener used by a target image view to determine what action to take when
     * the image view to sort interacts with it
     */
    private val targetOnDragListener = View.OnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_ENTERED -> dragInOutCallback(v, halfOpaque)
            DragEvent.ACTION_DRAG_EXITED -> dragInOutCallback(v, opaque)
            DragEvent.ACTION_DROP -> dropCallback(event, v)

            // DragEvent.ACTION_DRAG_ENDED &
            // DragEvent.ACTION_DRAG_LOCATION & DragEvent.ACTION_DRAG_STARTED
            else -> true
        }
    }

    /**
     * This callback is called when the image to sort is dropped on a target ImageView
     * @param event the DragEvent representing the interaction
     * @param v The ImageView representing the target category
     */
    private fun dropCallback(event: DragEvent, v: View): Boolean {
        setOpacity(v, opaque)

        val sortingCorrect = learningPresenter.isSortingCorrect(v as ImageView)
        audioFeedback.stopAndPrepareMediaPlayers()
        if (sortingCorrect) {
            audioFeedback.startCorrectFeedback()
            lifecycleScope.launch {
                learningPresenter.saveEvent(Event.SUCCESS)
                learningPresenter.updateForNextSorting(
                    parentActivity,
                    targetImageViews,
                    binding.learningToSort
                )
            }
        } else {
            audioFeedback.startIncorrectFeedback()
            lifecycleScope.launch {
                learningPresenter.saveEvent(Event.MISTAKE)
            }
        }
        return sortingCorrect
    }

    /**
     * This callback is called when the image to sort enters or leaves a target ImageView
     * @param v The image view that caused the event
     * @param opacity the opacity to which the given View will be set, between 0 and 1 (included)
     */
    private fun dragInOutCallback(v: View, opacity: Float): Boolean {
        setOpacity(v, opacity)
        return true
    }

    /**
     * Callback triggered when the image view holding the image to sort is touched.
     */
    private fun onImageToSortTouched(view: View, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                view.startDragAndDrop(
                    null,
                    View.DragShadowBuilder(view),
                    null,
                    0
                )
                true
            }
            else -> false
        }
    }

    companion object {
        /**
         * Set the opacity of the given view and force it to be redrawn
         * @param v The view whose opacity will be set
         * @param opacity The new opacity. Has to be in [0,1]
         */
        private fun setOpacity(v: View, opacity: Float) {
            require(opacity in 0.0f..1.0f)
            v.alpha = opacity
            v.invalidate()
        }
    }
}
