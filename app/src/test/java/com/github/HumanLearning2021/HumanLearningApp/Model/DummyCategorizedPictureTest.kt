package com.github.HumanLearning2021.HumanLearningApp.Model

import android.widget.ImageView
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*



class DummyCategorizedPictureTest {

    class NotDummy(override val name: String) :Category {
    }


    /*
    @Test
    fun notDummyCategory() = runBlockingTest {
        assertFailsWith(DummyCategorizedPicture(NotDummy("I'm no dummy!")).displayOn(null),
    }

     */
}