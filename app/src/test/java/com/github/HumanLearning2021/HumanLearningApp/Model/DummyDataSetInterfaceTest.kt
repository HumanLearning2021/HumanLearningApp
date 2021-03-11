package com.github.HumanLearning2021.HumanLearningApp.Model

<<<<<<< HEAD
=======
import kotlinx.coroutines.test.runBlockingTest
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70
import org.junit.Test

import org.junit.Assert.*

class DummyDataSetInterfaceTest {
    val dummyDataSetInterface1 = DummyDataSetInterface()
    val dummyDataSetInterface2 = DummyDataSetInterface()


    @Test
    fun getCurrentDataSet() {
        assert(dummyDataSetInterface1.equals(DummyDataSetInterface()))
    }

    @Test
<<<<<<< HEAD
    fun getPicture() {
=======
    fun getPicture() = runBlockingTest {
>>>>>>> 01f8cbdd24325478107733d762bf14268ed46a70
        assert(dummyDataSetInterface1.getPicture("fork") == DummyCategorizedPicture(Category("fork")))
    }

    @Test
    fun equalsHashCodeTest(){
        assert(dummyDataSetInterface1.hashCode() == dummyDataSetInterface2.hashCode() && dummyDataSetInterface1 == dummyDataSetInterface2)

    }
}