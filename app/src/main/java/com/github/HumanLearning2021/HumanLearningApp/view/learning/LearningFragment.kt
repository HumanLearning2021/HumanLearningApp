package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentLearningBinding
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.Id
import com.github.HumanLearning2021.HumanLearningApp.presenter.LearningPresenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LearningFragment: Fragment() {
    private lateinit var audioFeedback: LearningAudioFeedback
    private lateinit var datasetId: Id
    private lateinit var dataset: Dataset
    private val args: LearningFragmentArgs by navArgs()
    private var _binding: FragmentLearningBinding? = null
    private val binding get() = _binding!!


    @Inject
    lateinit var learningPresenter: LearningPresenter

    @Inject
    @Demo2Database
    lateinit var dbMgt: DatabaseManagement

    private lateinit var parentActivity: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        audioFeedback = LearningAudioFeedback(parentActivity.applicationContext)
        _binding = FragmentLearningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        datasetId = args.datasetId
        lifecycleScope.launch{
            dataset = dbMgt.getDatasetById(datasetId)!!
            learningPresenter.learningMode = args.learningMode
            learningPresenter.dataset = dataset
            initLearningViews()
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
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
        _binding = null

    }

    override fun onResume() {
        super.onResume()
        audioFeedback.initMediaPlayers()
    }

    override fun onPause() {
        super.onPause()
        audioFeedback.releaseMediaPlayers()
    }



    /**
     * This method initializes the image views containing the image to sort and the images
     * representing the target categories.
     */
    private fun initLearningViews() {
        lifecycleScope.launch {
            val cats = dataset.categories
            if (cats.size < 3) {
                // TODO : maybe allow fewer categories in the future
                Log.e(
                    this.javaClass.name, "There are fewer than 3 categories in the dataset",
                    IllegalStateException()
                )
            } else {
                val cat0 = cats.elementAt(0)

                initTargetCategory(R.id.learning_cat_0, cat0)
                initTargetCategory(R.id.learning_cat_1, cats.elementAt(1))
                initTargetCategory(R.id.learning_cat_2, cats.elementAt(2))

                initImageToSort(R.id.learning_to_sort, cat0)
            }

        }
    }


    private fun initImageView(catIvId: Int, cat: Category): ImageView {
        val catIv = when(catIvId) {
            R.id.learning_cat_0 -> binding.learningCat0
            R.id.learning_cat_1 -> binding.learningCat1
            R.id.learning_cat_2-> binding.learningCat2
            else -> binding.learningToSort
        }

        // TODO (next sprint) make this more robust
        // The mechanism to verify that the classification is correct is the comparison between
        // the contentDescription of the target ImageView and the text carried in the drag & drop
        // see LearningActivity::dropCallback
        catIv.contentDescription = cat.name
        Log.d(parentActivity.localClassName, "init contentDescription to ${cat.name}")

        if (catIvId == R.id.learning_to_sort) {
            lifecycleScope.launch {
                learningPresenter.displayNextPicture(parentActivity, catIv)
            }
        } else {
            lifecycleScope.launch {
                learningPresenter.displayTargetPicture(parentActivity, catIv, cat)
            }
        }

        return catIv
    }

    @SuppressLint("ClickableViewAccessibility")
    /**
     * This method initializes the image view containing the image to sort
     */
    private fun initImageToSort(catIvId: Int, cat: Category) {
        initImageView(catIvId, cat).setOnTouchListener{ e, v -> onImageToSortTouched(e, v)}
    }

    /**
     * This method initializes an image view representing a target category
     */
    private fun initTargetCategory(catIvId: Int, cat: Category) {
        initImageView(catIvId, cat).setOnDragListener(targetOnDragListener)
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
        val item: ClipData.Item = event.clipData.getItemAt(0)
        setOpacity(v, opaque)
        Log.d(parentActivity.localClassName, "dropped : ${item.text} on category: ${v.contentDescription}")

        // TODO (future sprint) make this more robust
        // the classification is considered correct if the text carried by the drag
        // is equal to the contentDescription of the target ImageView
        val res = item.text == v.contentDescription
        val self = parentActivity
        audioFeedback.stopAndPrepareMediaPlayers()
        if (res) {
            audioFeedback.startCorrectFeedback()
            lifecycleScope.launch {
                binding.learningToSort.let {
                    learningPresenter.displayNextPicture(
                        self,
                        it,
                    )
                }
            }
        } else {
            audioFeedback.startIncorrectFeedback()
        }
        return res
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

    private fun onImageToSortTouched(view: View, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // TODO (future sprint) make verification mechanism more robust
                val item = ClipData.Item(view.contentDescription)
                val dragData = ClipData(
                    getString(R.string.learning_clipdata_label),
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )
                val shadow = View.DragShadowBuilder(view)
                view.startDragAndDrop(dragData, shadow, null, 0)
                true
            }
            else -> false
        }
    }
    companion object {
        private fun setOpacity(v: View, opacity: Float) {
            v.alpha = opacity
            v.invalidate()
        }

    }
}
