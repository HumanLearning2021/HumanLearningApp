package com.github.HumanLearning2021.HumanLearningApp.Model

/**
 * A class representing users with admin privileges
 *
 * @param name the name of the admin
 */

class Admin(val name: String): User {

    override fun equals(other: Any?): Boolean {
        return other is Admin && other.name == name
    }

    override fun hashCode(): Int {
        return 17 + 31 * name.hashCode()
    }
}