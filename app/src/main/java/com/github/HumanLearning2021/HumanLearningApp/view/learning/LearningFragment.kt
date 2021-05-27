package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentLearningBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.model.learning.EvaluationModel
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import com.github.HumanLearning2021.HumanLearningApp.presenter.LearningPresenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class LearningFragment : Fragment() {
    private lateinit var audioFeedback: LearningAudioFeedback
    private lateinit var visualFeedback: LearningVisualFeedback
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

    /**
     * model for evaluation mode
     * A null value represents the fact that the learning mode is *not* EVALUATION
     *
     * TODO ideally the model would be wrapped inside the presenter (but no time right now)
     */
    private var evaluationModel: EvaluationModel? = null

    @Inject
    lateinit var authPresenter: AuthenticationPresenter

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @Inject
    @ProductionDatabaseName
    lateinit var dbName: String

    lateinit var dbMgt: DatabaseManagement

    @Inject
    lateinit var imageDisplayer: ImageDisplayer

    private lateinit var parentActivity: Activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            dbMgt = globalDatabaseManagement.accessDatabase(
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
        audioFeedback = LearningAudioFeedback(parentActivity.applicationContext)
        _binding = FragmentLearningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        datasetId = args.datasetId
        lifecycleScope.launch {
            dataset = dbMgt.getDatasetById(datasetId)!!
            if (args.learningMode == LearningMode.EVALUATION) {
                evaluationModel = EvaluationModel(dataset)
            }
            targetImageViews = updateTargetImageViews()
            learningPresenter =
                LearningPresenter(
                    dbMgt, args.learningMode, dataset, authPresenter, imageDisplayer,
                    lifecycleScope
                )
            learningPresenter.updateForNextSorting(
                targetImageViews,
                binding.learningToSort
            )
            // sets the listeners for the image views of the sorting
            setEventListeners()
        }

        visualFeedback = with(binding) {
            LearningVisualFeedback(
                lifecycleScope,
                baseColor = getColor(requireContext(), R.color.white),
                neutralColor = getColor(requireContext(), R.color.blue),
                positiveColor = getColor(requireContext(), R.color.light_green),
                negativeColor = getColor(requireContext(), R.color.red),
                // TODO remove !! once enclosing CardViews put in all layouts
                sourceCardView = learningToSortCv!!,
                targetCardViews = listOf(
                    learningCat0Cv!!,
                    learningCat1Cv!!,
                    learningCat2Cv!!
                )
            )
        }.also { it.sourceCardViewShouldBlink(true) }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }


    /**
     * Get the new target image views, depending on the state of the evaluation model if in
     * EVALUATION mode, otherwise according to the number of categories in the current dataset
     */
    private fun updateTargetImageViews(): List<ImageView> =
        if (evaluationModel != null) {
            adaptDisplayToNumberOfCategories(evaluationModel!!.getCurrentPhase())
        } else {
            adaptDisplayToNumberOfCategories(dataset.categories.size)
        }


    /**
     * This function adapts the display to the number of categories given
     * For example, if the dataset only has 2 categories, one of the categories will not be displayed
     * @param nbCategories number of categories that need to be displayed
     * @return the ImageViews that are visible on screen
     */
    private fun adaptDisplayToNumberOfCategories(nbCategories: Int): List<ImageView> {
        require(nbCategories > 0) {
            "A dataset used for learning should have at least one category"
        }
        val adjustVisibilities = { visibles: List<ImageView>, invisibles: List<ImageView> ->
            visibles.forEach { it.visibility = View.VISIBLE }
            invisibles.forEach { it.visibility = View.INVISIBLE }
            visibles
        }
        return with(binding) {
            when (nbCategories) {
                1 -> {
                    adjustVisibilities(listOf(learningCat1), listOf(learningCat0, learningCat2))
                }
                2 -> {
                    adjustVisibilities(listOf(learningCat0, learningCat1), listOf(learningCat2))
                }
                else -> adjustVisibilities(
                    listOf(learningCat0, learningCat1, learningCat2),
                    emptyList()
                )
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
                dbName
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
            DragEvent.ACTION_DROP -> dropCallback(v)

            // DragEvent.ACTION_DRAG_ENDED &
            DragEvent.ACTION_DRAG_ENDED -> {
                visualFeedback.dragEnded()
                true
            }
            // DragEvent.ACTION_DRAG_LOCATION & DragEvent.ACTION_DRAG_STARTED
            else -> true
        }
    }

    /**
     * This callback is called when the image to sort is dropped on a target ImageView
     * @param v The ImageView representing the target category
     */
    private fun dropCallback(v: View): Boolean {
        setOpacity(v, opaque)
        visualFeedback.dragEnded()

        val sortingCorrect = learningPresenter.isSortingCorrect(v as ImageView)
        audioFeedback.stopAndPrepareMediaPlayers()
        if (sortingCorrect) {
            audioFeedback.startCorrectFeedback()
            enclosingCardView(v)?.let { visualFeedback.startCorrectFeedback(it) }

            evaluationModel?.addSuccess()
            if (evaluationModel?.isEvaluationComplete() == true) {
                findNavController().navigate(
                    LearningFragmentDirections.actionLearningFragmentToEvaluationResultFragment(
                        evaluationModel!!.getCurrentEvaluationResult()
                    )
                )
            }
            // update image views in case addSuccess started the next evaluation phase
            targetImageViews = updateTargetImageViews()

            lifecycleScope.launch {
                learningPresenter.saveEvent(Event.SUCCESS)
                learningPresenter.updateForNextSorting(
                    targetImageViews,
                    binding.learningToSort
                )
            }
        } else {
            audioFeedback.startIncorrectFeedback()
            enclosingCardView(v)?.let { visualFeedback.startIncorrectFeedback(it) }
            evaluationModel?.addFailure()
            lifecycleScope.launch {
                learningPresenter.saveEvent(Event.MISTAKE)
            }
        }
        evaluationModel?.let { Log.d("Evaluation", it.getCurrentEvaluationResult().toString()) }
        return sortingCorrect
    }


    /**
     * Get the CardView that encloses the given ImageView.
     * @param v The image view whose containing CardView we want to find.
     * @return the enclosing CardView, or null if no enclosing CardView found
     */
    private fun enclosingCardView(v: ImageView) = with(binding) {
        when (v) {
            learningCat0 -> learningCat0Cv
            learningCat1 -> learningCat1Cv
            learningCat2 -> learningCat2Cv
            else -> null
        }
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
        visualFeedback.dragStarted()
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
