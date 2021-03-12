package com.github.HumanLearning2021.HumanLearningApp.Model

/**
 * A class representing users with admin privileges
 *
 * @param name the name of the admin (case-sensitive)
 */

data class Admin(val name: String): User {
}