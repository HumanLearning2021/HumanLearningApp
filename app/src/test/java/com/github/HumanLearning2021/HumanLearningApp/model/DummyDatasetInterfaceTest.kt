package com.github.HumanLearning2021.HumanLearningApp.model

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat.getDrawable
import com.github.HumanLearning2021.HumanLearningApp.R
import org.junit.Assert.*

import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.Assert.*
import java.lang.IllegalArgumentException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import java.io.Serializable
class DummyDataSetInterfaceTest {
    val dummyDatasetInterface1 = DummyDatasetInterface()
    val dummyDatasetInterface2 = DummyDatasetInterface()


    private val fork = DummyCategory("Fork")
    private val knife = DummyCategory("Knife")
    private val spoon = DummyCategory("Spoon")
    private val table = DummyCategory("Table")

    val categories: Set<Category> = mutableSetOf(fork, knife, spoon)

    private val forkPic = DummyCategorizedPicture(fork)
    private val knifePic = DummyCategorizedPicture(knife)
    private val spoonPic = DummyCategorizedPicture(spoon)
    private val tablePic = DummyCategorizedPicture(table)




    @Test
    fun getForkWorks() = runBlockingTest {
        val actual = dummyDatasetInterface1.getPicture(fork)
        val expected = forkPic
        assertEquals(actual, expected)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getPictureCategoryNotPresentThrows() = runBlockingTest {
        DummyDatasetInterface().getPicture(DummyCategory("plate"))
    }

    @Test
    fun getPictureCategoryEmpty() = runBlockingTest {
        assertThat(dummyDatasetInterface1.getPicture(dummyDatasetInterface1.putCategory("plate")), equalTo(null))
    }

    @Test
    fun putAndThenGetWorks() = runBlockingTest {

        dummyDatasetInterface1.putCategory("table")
        dummyDatasetInterface1.putPicture(DummySerializable(), table)

        assertThat(dummyDatasetInterface1.getPicture(table), equalTo(DummyCategorizedPicture(table)))

    }

    @Test(expected = IllegalArgumentException::class)
    fun putPictureCategoryNotPresentThrows() = runBlockingTest {
        dummyDatasetInterface1.putPicture(DummySerializable(), table)
    }

    @Test
    fun getCategoryPresent() = runBlockingTest {
        dummyDatasetInterface2.putCategory("table")
        assertThat(dummyDatasetInterface2.getCategory("table"), equalTo(DummyCategory("Table")))
    }

    @Test
    fun getCategoryNotPresent() = runBlockingTest {
        assertThat(dummyDatasetInterface2.getCategory("table"), equalTo(null));
    }

    @Test
    fun putCategoryNotPresent() = runBlockingTest {
        assertThat(dummyDatasetInterface1.putCategory("table"), equalTo(DummyCategory("table")))
    }

    @Test
    fun putCategoryAlreadyPresentChangesNothing() = runBlockingTest {
        dummyDatasetInterface1.putCategory("table")
        assertThat(dummyDatasetInterface1.putCategory("table"), equalTo(DummyCategory("table")))
    }

    @Test
    fun getCategoriesWorks() = runBlockingTest {
        dummyDatasetInterface1.putCategory("table")
        assertThat(dummyDatasetInterface1.getCategories(), equalTo(setOf(fork, spoon, knife, table)))
    }
}

class DummySerializable:Serializable {
}