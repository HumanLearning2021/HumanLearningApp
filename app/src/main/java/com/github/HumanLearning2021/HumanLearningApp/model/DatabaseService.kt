package com.github.HumanLearning2021.HumanLearningApp.model

/**
 * An interface representing the part of the model interacting with data sets
 */
interface DatabaseService {

    /**
     * A function to retrieve a picture from the database given a category
     *
     * @param category the category of the image to be retrieved
     * @return a CategorizedPicture from the desired category. Null if no picture of the desired
     * category is present in the database.
     * @throws IllegalArgumentException if the provided category is not present in the database
     */
    suspend fun getPicture(category: Category): CategorizedPicture?

    /**
     * Retrieves the picture associated to the specified category as its representative picture
     *
     * @param categoryId - the id of the category whose representative picture we want to retrieve
     * @return the representative picture as a categorizedPicture, can be null
     */
    suspend fun getRepresentativePicture(categoryId: Any): CategorizedPicture?

    /**
     * A function that allows to put a picture in the database
     *
     * @param picture the picture to put in the database
     * @param category the category to which the picture belongs
     * @return a Categorized picture built using 'picture' and 'category'
     * @throws IllegalArgumentException if the category provided is not present in the database
     */
    suspend fun putPicture(picture: android.net.Uri, category: Category): CategorizedPicture

    /**
     * A function to retrieve a category from the database
     *
     * @param categoryId the id of the desired category
     * @return the desired category if present, null otherwise
     */
    suspend fun getCategory(categoryId: Any): Category?

    /**
     * A function to add a category to the database
     *
     * @param categoryName the id of the category to add
     * @return the Category that was inserted
     */
    suspend fun putCategory(categoryName: String): Category

    /**
     * A function to retrieve the set of categories present in the database
     *
     * @return the set of categories present in the database
     */
    suspend fun getCategories(): Set<Category>

    /**
     * Retrieves all the pictures categorized with the specified category
     *
     * @param category - the category whose pictures we want to retrieve
     * @return the pictures categorized with the specified category
     * @throws IllegalArgumentException if the database does not contain the specified category
     */
    suspend fun getAllPictures(category: Category): Set<CategorizedPicture>

    /**
     * Remove the category from the database and from all the datasets contained in this database and using this category
     *
     * @param category - the category to remove from the database
     * @throws IllegalArgumentException if the database does not contain the specified category
     */
    suspend fun removeCategory(category: Category)

    /**
     * Removes the corresponding picture from the database
     *
     * @param picture - the picture to remove from the database
     * @throws IllegalArgumentException if the database does not contain the specified picture
     */
    suspend fun removePicture(picture: CategorizedPicture)

    /**
     * Creates a dataset and puts it into the database
     *
     *
     * @param name - the name of the dataset
     * @param categories - the categories of the dataset
     * @return the dataset which was created
     */
    suspend fun putDataset(name: String, categories: Set<Category>): Dataset

    /**
     * Gets a dataset from the database
     *
     * @param id - the name of the desired dataset
     * @return the dataset
     */
    suspend fun getDataset(id: Any): Dataset?

    /**
     * Deletes the specified dataset from the database
     *
     * @param id - the name of the dataset to delete
     * @throws IllegalArgumentException if there is no dataset of the specified id in the database
     */
    suspend fun deleteDataset(id: Any)

    /**
     * Adds a representative picture to the category. If there is already a representative picture assigned it will be overwritten.
     *
     * @param picture - the picture to put as a representative
     * @param category - the category whose representative picture we want to change
     * @throws IllegalArgumentException if the database does not contain the specified category
     */
    suspend fun putRepresentativePicture(picture: android.net.Uri, category: Category)

    /**
     * Retrieves all of the available datasets
     *
     * @return a set containing all off the available datasets
     */
    fun getDatasets(): Set<Dataset>
}