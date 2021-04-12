package com.github.HumanLearning2021.HumanLearningApp.model

import org.junit.Assert.*
import org.junit.Test


class DummyCategoryTest {

    private val spoon1 = DummyCategory("Spoon", "Spoon")
    private val spoon2 = DummyCategory("Spoon", "Spoon")
    private val spoon3 = DummyCategory("Spoon", "Spoon")
    private val knife = DummyCategory("Knife", "Knife")


    @Test
    fun getName() {
        assertEquals(spoon2.name, "Spoon")
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