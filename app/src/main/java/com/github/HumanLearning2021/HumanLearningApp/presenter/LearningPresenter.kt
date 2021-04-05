package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.*
import com.github.HumanLearning2021.HumanLearningApp.view.LearningMode
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.lang.IllegalArgumentException


class DummyLearningPresenter(
    private val learningMode: LearningMode
) {
    private var previousCategory : Category? = null
    private val databaseService = DummyDatabaseService()

    init {
        runBlocking {
            //allows to call displayNextPicture on initialization
            previousCategory = databaseService.getCategory("Spoon")
        }
    }

    suspend fun displayNextPicture(activity: Activity, view: ImageView) {
        /*
        TODO: should the activity be a property of the class instead? Since the presenter and the activity have a 1-1 relationship
         */
        val cats = databaseService.getCategories()
        var rndCat: Category?
        do {
            rndCat = cats.random()
        } while (previousCategory == rndCat)
        Log.d("displayNextPicture", "previous : $previousCategory, curr : $rndCat")
        previousCategory = rndCat

        val nextPicture = databaseService.getPicture(rndCat!!)
        nextPicture!!.displayOn(activity, view)
        view.contentDescription = rndCat!!.name
        view.invalidate()
    }


    suspend fun displayTargetPicture(activity: Activity, view: ImageView, categoryName: String){
        if(learningMode == LearningMode.PRESENTATION)
            databaseService.getPicture(databaseService.getCategory(categoryName)!!)?.displayOn(activity, view)
        else
           databaseService.getCategory(categoryName)?.representativePicture?.displayOn(activity, view)
    }
}
