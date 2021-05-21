package com.github.HumanLearning2021.HumanLearningApp.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test


class DummyCategoryTest {

    private val spoon1 = Category("Spoon", "Spoon")
    private val spoon2 = Category("Spoon", "Spoon")
    private val spoon3 = Category("Spoon", "Spoon")
    private val knife = Category("Knife", "Knife")


    @Test
    fun getName() {
        assertEquals(spoon2.name, "Spoon")
    }

    @Test
    fun testEqualsExact() {
        assertEquals(spoon1, spoon3)
    }

    @Test
    fun testEqualsNotEqual() {
        assertNotEquals(spoon1, knife)
    }
}