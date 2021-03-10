package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.R
import java.lang.IllegalStateException
import kotlin.random.Random

class LearningPresenter {
    companion object {
        fun onImageToSortTouched(view : View, event: MotionEvent): Boolean{
            val logTag = "onImageToSortTouched"
            val clipDataLabel = "My Clip Data"
            return when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    val item = ClipData.Item(view.tag as CharSequence)
                    Log.d(logTag, item.toString())
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
            val logTag = "targetOnDragListener"
            val opaque = 1.0f
            val halfOpaque = opaque/2
            when(event.action){
                DragEvent.ACTION_DRAG_STARTED -> true
                DragEvent.ACTION_DRAG_ENTERED -> {
                    (v as? ImageView)?.alpha = halfOpaque
                    v.invalidate() // to force redraw
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> {
                    (v as? ImageView)?.alpha = opaque
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    (v as? ImageView)?.alpha = opaque
                    v.invalidate()
                    when (val dragData = item.text) {
                        v.tag -> {
                            Log.d(logTag, "Dragged data is $dragData")
                            true
                        }
                        else -> {
                            Log.d(logTag, "dragData : $dragData vs tag ${v.tag}")
                            false
                        }
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    if(event.result){
                        when(val iv = event.localState){
                            is ImageView -> displayNextPicture(iv)
                            else -> throw IllegalStateException("The local state of the drag " +
                                    "and drop should be the ImageView to sort")
                        }
                        true
                    }else{
                        false
                    }
                }
                else -> {
                    Log.e(logTag, "Unkown action type")
                    false
                }
            }
        }

        private fun displayNextPicture(view: ImageView) {
            // TODO this is a dirty hack, replace with model call
            val colorStrings = listOf("#ffff0000", "#ff00ff00", "#ff0000ff")
            val colorInts = listOf(R.color.red, R.color.green, R.color.blue)
            val rCol = Random.nextInt(from = 0, until = 2)
            view.setImageResource(colorInts[rCol])
            view.tag = colorStrings[rCol]
            view.invalidate()
        }
    }
}