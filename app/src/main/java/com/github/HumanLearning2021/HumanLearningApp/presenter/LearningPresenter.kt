package com.github.HumanLearning2021.HumanLearningApp.presenter

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseService
import com.github.HumanLearning2021.HumanLearningApp.model.DummyCategory
import com.github.HumanLearning2021.HumanLearningApp.view.LearningMode
import java.lang.Exception
import java.lang.IllegalArgumentException


class LearningPresenter(
    /*
    TODO: see with Niels: should the databaseService really be a property? e.g. if we want to change the database from which to display pictures from without throwing away the presenter
     */
    private val databaseService: DatabaseService,
    private val learningMode: LearningMode
) {
    private var previousCategory : Category? = null
    suspend fun displayNextPicture(activity: Activity, view: ImageView) {
        /*
        TODO: should the view be a property of the class instead? Since the presenter and the view have a 1-1 relationship
         */
        val cats = databaseService.getCategories()
        var rndCat: Category?
        do {
            rndCat = cats.random()
        } while (previousCategory == rndCat)
        Log.d("displayNextPicture", "previous : $previousCategory, curr : $rndCat")
        previousCategory = rndCat

        val nextPicture = databaseService.getPicture(rndCat!!) // TODO change this
        nextPicture!!.displayOn(activity, view)
        view.contentDescription = rndCat!!.name
        view.invalidate()
    }

    /**
     * Allows to retrieve a picture fron the dummy dataset
     *
     * @param categoryName the name of the category of the picture to retrieve. Can be "knife", "fork", or "spoon"
     * @throws IllegalArgumentException if the string provided doesn't match any of "knife", "fork", or "spoon"
     */
    suspend fun displayTargetPicture(activity: Activity, view: ImageView, categoryName: String){
        databaseService.getPicture(DummyCategory(categoryName, categoryName,null))!!.displayOn(activity, view)
    }
}
