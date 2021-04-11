package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.BuildConfig
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatabaseService
import com.github.HumanLearning2021.HumanLearningApp.presenter.LearningPresenter
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class LearningActivity : AppCompatActivity() {

    private lateinit var learningPresenter: LearningPresenter
    private lateinit var learningMode: LearningMode
    private lateinit var audioFeedback: LearningAudioFeedback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)
        learningMode =
            intent.getSerializableExtra(LearningSettingsActivity.EXTRA_LEARNING_MODE) as LearningMode
        val maybeDataset = intent.getParcelableExtra<Dataset>(LearningDatasetSelectionActivity.EXTRA_SELECTED_DATASET)
        var dataset: Dataset? = null
        if (maybeDataset != null){
            dataset = maybeDataset
        }else{
            Log.e(this.localClassName, "The intent launching the LearningActivity didn't contain" +
                    " a Dataset", IllegalStateException())
        }
        learningPresenter = LearningPresenter(this, learningMode, dataset!!)
        initLearningViews()
        audioFeedback = LearningAudioFeedback(applicationContext)
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
            val cats = DummyDatabaseService().getCategories()
            if (cats.size < 3) {
                // TODO : maybe allow fewer categories in the future
                Log.e(this.javaClass.name, "There are fewer than 3 categories in the dataset",
                    IllegalStateException())
            }

            val cat0 = cats.elementAt(0)

            initTargetCategory(R.id.learning_cat_0, cat0)
            initTargetCategory(R.id.learning_cat_1, cats.elementAt(1))
            initTargetCategory(R.id.learning_cat_2, cats.elementAt(2))

            initImageToSort(R.id.learning_im_to_sort, cat0)
        }
    }

    private fun initImageView(catIvId: Int, cat: Category): ImageView {
        val catIv = findViewById<ImageView>(catIvId)
        catIv.contentDescription = cat.name

        if (catIvId == R.id.learning_im_to_sort) {
            lifecycleScope.launch {
                learningPresenter.displayNextPicture(catIv)
            }
        } else {
            lifecycleScope.launch {
                learningPresenter.displayTargetPicture(catIv, cat)
            }
        }

        return catIv
    }

    private fun initImageToSort(catIvId: Int, cat: Category) {
        initImageView(catIvId, cat).setOnTouchListener(LearningActivity::onImageToSortTouched)
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
        audioFeedback.stopAndPrepareMediaPlayers()
        if (res) {
            audioFeedback.startCorrectFeedback()
            lifecycleScope.launch {
                learningPresenter.displayNextPicture(
                    findViewById(R.id.learning_im_to_sort),
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

    }
}