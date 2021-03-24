package com.github.HumanLearning2021.HumanLearningApp.firestore

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.model.hasCategory
import com.github.HumanLearning2021.HumanLearningApp.model.hasName
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.Assert.assertThat
import java.io.File


class FirestoreDatabaseServiceTest : TestCase() {
    lateinit var demoInterface: FirestoreDatabaseService
    lateinit var scratchInterface: FirestoreDatabaseService

    override fun setUp() {
        demoInterface = FirestoreDatabaseService("demo")
        scratchInterface = FirestoreDatabaseService("scratch")
    }

    fun test_getCategories() = runBlocking {
        val cats = demoInterface.getCategories()
        assertThat(cats, hasItem(hasName("Pomme")))
    }

    fun test_getCategory() = runBlocking {
        val cat = demoInterface.getCategory("Pomme")
        assertThat(cat, hasName("Pomme"))
    }

    fun test_getPicture() = runBlocking {
        val appleCategory = demoInterface.getCategories().find { it.name == "Pomme" }
        requireNotNull(appleCategory, { "category of apples no found in demo dataset" })
        val pic = demoInterface.getPicture(appleCategory)
        assertThat(pic, hasCategory(equalTo(appleCategory)))
    }

    fun test_putCategory() = runBlocking {
        val cat = scratchInterface.putCategory("Poire")
        assertThat(cat, hasName("Poire"))
    }

    fun test_putPicture() = runBlocking {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cat = scratchInterface.putCategory("Poire")

        val tmp = File.createTempFile("meow", ".png")
        val pic = try {
            ctx.resources.openRawResource(R.drawable.fork).use { img ->
                tmp.outputStream().use {
                    img.copyTo(it)
                }
            }
            val uri = Uri.fromFile(tmp)
            scratchInterface.putPicture(uri, cat)
        } finally {
            tmp.delete()
        }
        assertThat(pic, hasCategory(equalTo(cat)))
    }

    //TODO()
    fun test_putDataset() = runBlocking {
        assert(true)
    }

    //TODO()
    fun test_getDataset() = runBlocking {
        assert(true)
    }
}

