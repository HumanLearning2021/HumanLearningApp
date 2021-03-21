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
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.DummyDatasetInterface
import com.github.HumanLearning2021.HumanLearningApp.presenter.DummyUIPresenter
import com.github.HumanLearning2021.HumanLearningApp.presenter.LearningPresenter
import kotlinx.coroutines.launch

class LearningActivity : AppCompatActivity() {

    private val dummyPres = DummyUIPresenter()
    private val learningPresenter = LearningPresenter(DummyDatasetInterface(), lifecycleScope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)

        initTargetCategory(R.id.learning_cat_0, "knife")
        initTargetCategory(R.id.learning_cat_1, "spoon")
        initTargetCategory(R.id.learning_cat_2, "fork")

        initImageToSort(R.id.learning_im_to_sort, "fork")

    }

    private fun initImageView(catIvId: Int, catName: String): ImageView {
        val catIv = findViewById<ImageView>(catIvId)
        catIv.contentDescription = catName
        lifecycleScope.launch {
            dummyPres.getPicture(catName)?.displayOn(this@LearningActivity, catIv)
        }
        return catIv
    }

    private fun initImageToSort(catIvId: Int, catName: String) {
        initImageView(catIvId, catName).setOnTouchListener(LearningActivity::onImageToSortTouched)
    }

    private fun initTargetCategory(catIvId: Int, catName: String) {
        initImageView(catIvId, catName).setOnDragListener(targetOnDragListener)
    }

    private val opaque = 1.0f
    private val halfOpaque = opaque / 2
    private val targetOnDragListener = View.OnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_ENTERED -> dragEnteredCallback(v)
            DragEvent.ACTION_DRAG_EXITED -> dragExitedCallback(v)
            DragEvent.ACTION_DROP -> dropCallback(event, v)
            DragEvent.ACTION_DRAG_ENDED -> dragEndedCallback(event)

            // DragEvent.ACTION_DRAG_LOCATION & DragEvent.ACTION_DRAG_STARTED
            else -> true
        }
    }

    private fun dragEndedCallback(event: DragEvent) = if (event.result) {
        when (val iv = event.localState) {
            is ImageView ->
                lifecycleScope.launch {
                    learningPresenter.displayNextPicture(this@LearningActivity, iv)
                }
            else -> throw IllegalStateException(
                "The local state of the drag " +
                        "and drop should be of type ImageView"
            )
        }
        true
    } else {
        false
    }

    private fun dropCallback(event: DragEvent, v: View): Boolean {
        val item: ClipData.Item = event.clipData.getItemAt(0)
        v.alpha = opaque
        v.invalidate()
        Log.d("dropCallback", "${item.text} vs ${v.contentDescription}")
        return item.text == v.contentDescription
    }

    private fun dragInOutCallback(v: View, opacity: Float):Boolean{
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
                    view.startDragAndDrop(dragData, shadow, view, 0)
                    true
                }
                else -> false
            }
        }

    }
}