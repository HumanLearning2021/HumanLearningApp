package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.content.ClipData
import android.content.ClipDescription
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.R
import kotlin.random.Random

class LearningPresenter {
    companion object {
        fun onImageToSortTouched(view : View, event: MotionEvent): Boolean{
            val clipDataLabel = "My Clip Data"
            return when(event.action){
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

        val targetOnDragListener = View.OnDragListener { v, event ->
            val opaque = 1.0f
            val halfOpaque = opaque/2
            when(event.action){
                DragEvent.ACTION_DRAG_STARTED -> true
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.alpha = halfOpaque
                    v.invalidate() // to force redraw
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> {
                    v.alpha = opaque
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    v.alpha = opaque
                    v.invalidate()
                    item.text == v.contentDescription
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    if(event.result){
                        when(val iv = event.localState){
                            is ImageView -> displayNextPicture(iv)
                            else -> throw IllegalStateException("The local state of the drag " +
                                    "and drop should be of type ImageView")
                        }
                        true
                    }else{
                        false
                    }
                }
                else -> {
                    Log.e("targetOnDragListener", "Unkown action type")
                    false
                }
            }
        }

        private fun displayNextPicture(view: ImageView) {
            // TODO this is a dirty hack, replace with model call
            fun gS(id:Int) = view.context.getString(id)
            val colorStrings = listOf(gS(R.string.learning_cat_0_descr),
                    gS(R.string.learning_cat_1_descr), gS(R.string.learning_cat_2_descr))
            val colorInts = listOf(R.color.red, R.color.green, R.color.blue)
            val rCol = Random.nextInt(from = 0, until = 2)
            view.setImageResource(colorInts[rCol])
            view.contentDescription = colorStrings[rCol]
            view.invalidate()
        }
    }
}