package com.github.HumanLearning2021.HumanLearningApp.Model

import android.graphics.drawable.Drawable

val utensil = Category("Utensil", null)
val fork = Category("Fork", utensil)
val knife = Category("Knife", utensil)
val spoon = Category("Spoon", utensil)
val admin = Admin("HLStaff")

class DummyDataSet : DataSet("Dummy", admin, setOf(fork, knife, spoon), 1) {
    val forkPic = CategorizedPicture(fork)
    val knifePic = CategorizedPicture(knife)
    val spoonPic = CategorizedPicture(spoon)


    override fun getPicture(category: Category): CategorizedPicture =
            when (category) {
                fork -> forkPic
                knife -> knifePic
                else -> spoonPic
            }

}