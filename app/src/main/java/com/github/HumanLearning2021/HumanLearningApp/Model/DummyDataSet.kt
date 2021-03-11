package com.github.HumanLearning2021.HumanLearningApp.Model

import android.graphics.drawable.Drawable

val utensil = Category("Utensil", null)
val fork = Category("Fork", utensil)
val knife = Category("Knife", utensil)
val spoon = Category("Spoon", utensil)
val admin = Admin("HLStaff")

class DummyDataSet : DataSet("Dummy", admin, setOf(fork, knife, spoon), 1) {
    val forkPic = DummyCategorizedPicture(fork)
    val knifePic = DummyCategorizedPicture(knife)
    val spoonPic = DummyCategorizedPicture(spoon)


    override fun getPicture(category: Category): CategorizedPicture =
            when (category) {
                fork -> forkPic
                knife -> knifePic
                else -> spoonPic
            }

}