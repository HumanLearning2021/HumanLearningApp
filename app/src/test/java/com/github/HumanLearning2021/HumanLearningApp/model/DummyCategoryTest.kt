package com.github.HumanLearning2021.HumanLearningApp.model

import org.junit.Assert.*
import org.junit.Test


class DummyCategoryTest {

    val spoon1 = DummyCategory("Spoon", null)
    val spoon2 = DummyCategory("Spoon", null)
    val spoon3 = DummyCategory("Spoon", null)
    val knife = DummyCategory("Knife", null)


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