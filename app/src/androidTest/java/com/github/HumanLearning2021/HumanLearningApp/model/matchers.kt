package com.github.HumanLearning2021.HumanLearningApp.model

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

private class HasName(private val nameMatcher: Matcher<String>) :
    TypeSafeMatcher<Category>() {
    override fun matchesSafely(cat: Category) = nameMatcher.matches(cat.name)
    override fun describeTo(description: Description) {
        description.appendText("has name").appendDescriptionOf(nameMatcher)
    }
}

fun hasName(expectedName: String) = hasName(Matchers.equalTo(expectedName))
fun hasName(nameMatcher: Matcher<String>): Matcher<Category?> = HasName(nameMatcher)

private class HasCategory(private val categoryMatcher: Matcher<Category>) :
    TypeSafeMatcher<CategorizedPicture>() {
    override fun matchesSafely(pic: CategorizedPicture) = categoryMatcher.matches(pic.category)
    override fun describeTo(description: Description) {
        description.appendText("has category").appendDescriptionOf(categoryMatcher)
    }
}

fun hasCategory(categoryMatcher: Matcher<Category>): Matcher<CategorizedPicture?> =
    HasCategory(categoryMatcher)