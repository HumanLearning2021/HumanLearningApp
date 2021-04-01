package com.github.HumanLearning2021.HumanLearningApp.model

/**
 * Entry point to retrieve data from the underlying database as well as modify it.
 */
interface DatabaseManagement {

    /**
     * A function to retrieve a picture from the underlying database given a category
     *
     * @param category the category of the image to be retrieved
     * @return a CategorizedPicture from the desired category. Null if no picture of the desired
     * category is present in the underlying database.
     * @throws IllegalArgumentException if the provided category is not present in the underlying
     */
    suspend fun getPicture(category: Category): CategorizedPicture?

    /**
     * A function that allows to put a picture in the underlying database
     *
     * @param picture the picture to put in the underlying database
     * @param category the category to which the picture belongs
     * @return a Categorized picture built using 'picture' and 'category'
     * @throws IllegalArgumentException if the category provided is not present in the underlying database
     */
    suspend fun putPicture(picture: android.net.Uri, category: Category): CategorizedPicture

    /**
     * A function to retrieve a category from the underlying database
     *
     * @param categoryId the id of the desired category
     * @return the desired category if present, null otherwise
     */
    suspend fun getCategory(categoryId: Any): Category?

    /**
     * A function to retrieve a category from the underlying database
     *
     * @param categoryName the name of the desired category
     * @return the categories of the specified name, can be empty
     */
    suspend fun getCategory(categoryName: String): Collection<Category>

    /**
     * A function to add a category to the underlying database
     *
     * @param categoryName the name of the category to add
     * @return the Category that was inserted
     */
    suspend fun putCategory(categoryName: String): Category

    /**
     * A function to retrieve the set of categories present in the underlying database
     *
     * @return the set of categories present in the underlying database
     */
    suspend fun getCategories(): Set<Category>

    /**
     * Retrieves the representative picture of the specified category
     *
     * @param category - the category whose representative picture we want to retrieve
     * @return the representative picture associated to the specified category
     * @throws IllegalArgumentException if the underlying database does not contain the specified category
     */
    suspend fun getRepresentativePicture(category: Category): CategorizedPicture?

    /**
     * Retrieves all the pictures categorized with the specified category
     *
     * @param category - the category whose pictures we want to retrieve
     * @return the pictures categorized with the specified category
     * @throws IllegalArgumentException if the underlying database does not contain the specified category
     */
    suspend fun getAllPictures(category: Category): Set<CategorizedPicture>

    /**
     * Remove the category from the underlying database
     *
     * @param category - the category to remove from the underlying database
     * @throws IllegalArgumentException if the underlying database does not contain the specified category
     */
    suspend fun removeCategory(category: Category)

    /**
     * Removes the corresponding picture from the underlying database
     *
     * @param picture - the picture to remove from the underlying database
     * @throws IllegalArgumentException if the underlying database does not contain the specified picture
     */
    suspend fun removePicture(picture: CategorizedPicture)

    /**
     * Creates a dataset and puts it into the underlying database
     *
     * @param name - the name of the dataset
     * @param categories - the categories of the dataset
     * @return the dataset which was created
     * @throws IllegalArgumentException if there is already a dataset with this id in the underlying database
     */
    suspend fun putDataset(name: String, categories: Set<Category>): Dataset

    /**
     * Gets a dataset from the underlying database
     *
     * @param id - the name of the desired dataset
     * @return the dataset
     */
    suspend fun getDataset(id: Any): Dataset?

    /**
     * Get the dataset of the specified name from the underlying database
     *
     * @param name - the name of the desired dataset
     * @return all the matching datasets
     */
    suspend fun getDataset(datasetName: String): Collection<Dataset>?

    /**
     * Deletes the specified dataset from the underlying database
     *
     * @param id - the name of the dataset to delete
     * @throws IllegalArgumentException if there is no dataset of the specified id in the underlying database
     */
    suspend fun deleteDataset(id: Any)

    /**
     * Adds a representative picture to the category. If there is already a representative picture assigned it will be overwritten.
     *
     * @param picture - the picture to put as a representative
     * @param category - the category whose representative picture we want to change
     * @throws IllegalArgumentException if the underlying database does not contain the specified category
     */
    suspend fun putRepresentativePicture(picture: android.net.Uri, category: Category)

    /**
     * Adds a representative picture to the category. If there is already a representative picture assigned it will be overwritten.
     *
     * @param picture - the picture to put as a representative
     * @throws IllegalArgumentException if the underlying database does not contain the category of the picture
     */
    suspend fun putRepresentativePicture(picture: CategorizedPicture)

    /**
     * Retrieves all of the available datasets
     *
     * @return a set containing all off the available datasets
     */
    suspend fun getDatasets(): Set<Dataset>

    /**
     * Retrieves the names of all the datasets available
     *
     * @return a set containing the names off all the available datasets
     */
    suspend fun getDatasetNames(): Collection<String>

    /**
     * Retrieves the ids of all the datasets available
     *
     * @return a set containing the ids off all the available datasets
     */
    suspend fun getDatasetIds(): Set<Any>
}
