package com.github.HumanLearning2021.HumanLearningApp.view.learning

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.DummyDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.presenter.LearningPresenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class LearningFragment: Fragment() {
    private lateinit var audioFeedback: LearningAudioFeedback
    private lateinit var dataset: Dataset

    @Inject
    lateinit var learningPresenter: LearningPresenter

    @Inject
    @DummyDatabase
    lateinit var dbMgt: DatabaseManagement

    private lateinit var activity: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = requireActivity()
        return inflater.inflate(R.layout.fragment_learning, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val maybeDataset =
            arguments?.getParcelable<Dataset>(ARG_DSET)
        if (maybeDataset != null) {
            dataset = maybeDataset
        } else {
            Log.e(
                requireActivity().localClassName, "The intent launching the LearningActivity didn't contain" +
                        " a Dataset", IllegalStateException()
            )
        }
        learningPresenter.learningMode =
            arguments?.getSerializable(ARG_LEARNING_MODE) as LearningMode
        learningPresenter.dataset = dataset
        initLearningViews()
        audioFeedback = LearningAudioFeedback(activity.applicationContext)
    }

    override fun onResume() {
        super.onResume()
        audioFeedback.initMediaPlayers()
    }

    override fun onPause() {
        super.onPause()
        audioFeedback.releaseMediaPlayers()
    }



    private fun initLearningViews() {
        lifecycleScope.launch {
            val cats = dataset.categories
            if (cats.size < 3) {
                // TODO : maybe allow fewer categories in the future
                Log.e(
                    this.javaClass.name, "There are fewer than 3 categories in the dataset",
                    IllegalStateException()
                )
            }else{
                val cat0 = cats.elementAt(0)

                initTargetCategory(R.id.learning_cat_0, cat0)
                initTargetCategory(R.id.learning_cat_1, cats.elementAt(1))
                initTargetCategory(R.id.learning_cat_2, cats.elementAt(2))

                initImageToSort(R.id.learning_im_to_sort, cat0)
            }

        }
    }

    private fun initImageView(catIvId: Int, cat: Category): ImageView {
        val catIv = view?.findViewById<ImageView>(catIvId)!!
        catIv.contentDescription = cat.name
        val self = activity

        if (catIvId == R.id.learning_im_to_sort) {
            lifecycleScope.launch {
                learningPresenter.displayNextPicture(self, catIv)
            }
        } else {
            lifecycleScope.launch {
                learningPresenter.displayTargetPicture(self, catIv, cat)
            }
        }

        return catIv
    }

    private fun initImageToSort(catIvId: Int, cat: Category) {
        initImageView(catIvId, cat).setOnTouchListener(Companion::onImageToSortTouched)
    }

    private fun initTargetCategory(catIvId: Int, cat: Category) {
        initImageView(catIvId, cat).setOnDragListener(targetOnDragListener)
    }

    private val opaque = 1.0f
    private val halfOpaque = opaque / 2
    private val targetOnDragListener = View.OnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_ENTERED -> dragEnteredCallback(v)
            DragEvent.ACTION_DRAG_EXITED -> dragExitedCallback(v)
            DragEvent.ACTION_DROP -> dropCallback(event, v)

            // DragEvent.ACTION_DRAG_ENDED &
            // DragEvent.ACTION_DRAG_LOCATION & DragEvent.ACTION_DRAG_STARTED
            else -> true
        }
    }

    private fun dropCallback(event: DragEvent, v: View): Boolean {
        val item: ClipData.Item = event.clipData.getItemAt(0)
        v.alpha = opaque
        v.invalidate()
        Log.d("dropCallback", "${item.text} vs ${v.contentDescription}")
        val res = item.text == v.contentDescription
        val self = activity
        audioFeedback.stopAndPrepareMediaPlayers()
        if (res) {
            audioFeedback.startCorrectFeedback()
            lifecycleScope.launch {
                learningPresenter.displayNextPicture(
                    self,
                    view?.findViewById(R.id.learning_im_to_sort)!!,
                )
            }
        } else {
            audioFeedback.startIncorrectFeedback()
        }
        return res
    }


    private fun dragInOutCallback(v: View, opacity: Float): Boolean {
        v.alpha = opacity
        v.invalidate()
        return true
    }

    private fun dragExitedCallback(v: View): Boolean = dragInOutCallback(v, opaque)

    private fun dragEnteredCallback(v: View): Boolean = dragInOutCallback(v, halfOpaque)

    companion object {

        fun onImageToSortTouched(view: View, event: MotionEvent): Boolean {
            val clipDataLabel = "My Clip Data"
            return when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val item = ClipData.Item(view.contentDescription)
                    val dragData = ClipData(
                        clipDataLabel,
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

        // to retrieve arguments
        const val ARG_DSET = "argDataset"
        const val ARG_LEARNING_MODE = "argLearningMode"

        fun newInstance(dSet: Parcelable, lMode: Serializable): LearningFragment{
            val args = Bundle()
            args.putParcelable(ARG_DSET, dSet)
            args.putSerializable(ARG_LEARNING_MODE, lMode)
            val fragment = LearningFragment()
            fragment.arguments = args
            return fragment
        }


    }






}