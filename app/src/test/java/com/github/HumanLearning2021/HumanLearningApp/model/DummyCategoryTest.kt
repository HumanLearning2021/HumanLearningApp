package com.github.HumanLearning2021.HumanLearningApp.model

import org.junit.Assert.*
import org.junit.Test


class DummyCategoryTest {

    val spoon1 = DummyCategory("Spoon")
    val spoon2 = DummyCategory("Spoon")
    val spoon3 = DummyCategory("Spoon")
    val knife = DummyCategory("Knife")


    @Test
    fun getName() {
        assertEquals(spoon2.name, "Spoon")
    }

    @Test
    fun testEqualsLowerCase() {
        assertEquals(spoon1, spoon2)
    }

    @Test
    fun testEqualsExact(){
        assertEquals(spoon1, spoon3)
    }

    @Test
    fun testEqualsNotEqual(){
        assertNotEquals(spoon1, knife)
    }
}