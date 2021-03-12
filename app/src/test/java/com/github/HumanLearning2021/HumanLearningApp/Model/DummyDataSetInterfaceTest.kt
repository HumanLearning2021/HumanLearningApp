package com.github.HumanLearning2021.HumanLearningApp.Model


import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*

class DummyDataSetInterfaceTest {
    val dummyDataSetInterface1 = DummyDataSetInterface()
    val dummyDataSetInterface2 = DummyDataSetInterface()


    @Test
    fun getCurrentDataSetTest() {
        assert(dummyDataSetInterface1.equals(DummyDataSetInterface()))
    }

    @Test

    fun getPictureTest() = runBlockingTest {
        val actual = dummyDataSetInterface1.getPicture("Fork")
        val expected = DummyCategorizedPicture(Category("Fork"))
        assert(actual == expected)
    }

    @Test
    fun equalsHashCodeTest(){
        assert(dummyDataSetInterface1.hashCode() == dummyDataSetInterface2.hashCode() && dummyDataSetInterface1 == dummyDataSetInterface2)

    }
}