package com.github.HumanLearning2021.HumanLearningApp.Model

import org.junit.Test

import org.junit.Assert.*



class CategoryTest {

    @Test
    fun testEquals() {
        assert(Category("fork").equals(Category("fork")))
    }

    @Test
    fun testEqualsUpperCase() {
        assert(!Category("fork").equals(Category("knife")))
    }
}