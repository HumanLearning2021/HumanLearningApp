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
    private val databaseManagement = DummyDatabaseManagement.staticDummyDatabaseManagement


    suspend fun displayNextPicture(activity: Activity, view: ImageView) {
        /*
        TODO: should the activity be a property of the class instead? Since the presenter and the activity have a 1-1 relationship
         */
        val cats = databaseManagement.getCategories()
        var rndCat: Category?
        do {
            rndCat = cats.random()
        } while (previousCategory == rndCat)
        Log.d("displayNextPicture", "previous : $previousCategory, curr : $rndCat")
        previousCategory = rndCat

        val nextPicture = when(learningMode){
            LearningMode.REPRESENTATION -> databaseManagement.getPicture(rndCat!!)
            LearningMode.PRESENTATION -> databaseManagement.getRepresentativePicture(rndCat!!.name)
        }
        nextPicture!!.displayOn(activity, view)
        view.contentDescription = rndCat!!.name
        view.invalidate()
    }


    suspend fun displayTargetPicture(activity: Activity, view: ImageView, categoryName: String){
        databaseService.getPicture(databaseManagement.getCategoryById(categoryName)!!)?.displayOn(activity, view)
    }
}


/*
class DummyLearningPresenter(
    private val learningMode: LearningMode
) {
    private var previousCategory : Category? = null
    private val databaseManagement = DummyDatabaseManagement.staticDummyDatabaseManagement



    suspend fun displayNextPicture(activity: Activity, view: ImageView) {
        /*
        TODO: should the activity be a property of the class instead? Since the presenter and the activity have a 1-1 relationship
         */
        val cats = databaseManagement.getCategories()
        var rndCat: Category?
        do {
            rndCat = cats.random()
        } while (previousCategory == rndCat)
        Log.d("displayNextPicture", "previous : $previousCategory, curr : $rndCat")
        previousCategory = rndCat

        val nextPicture = when(learningMode){
            LearningMode.REPRESENTATION -> databaseManagement.getPicture(rndCat!!)
            LearningMode.PRESENTATION -> databaseManagement.getRepresentativePicture(rndCat!!.name)
        }

        nextPicture!!.displayOn(activity, view)
        view.contentDescription = rndCat!!.name
        view.invalidate()
    }


    suspend fun displayTargetPicture(activity: Activity, view: ImageView, categoryName: String){
        databaseManagement.getRepresentativePicture(categoryName)?.displayOn(activity, view)
    }
}
 */