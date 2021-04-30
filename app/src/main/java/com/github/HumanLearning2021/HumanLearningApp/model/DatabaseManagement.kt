package com.github.HumanLearning2021.HumanLearningApp.model

import dagger.Binds

/**
 * Entry point to retrieve data from the underlying database as well as modify it.
 */
interface DatabaseManagement {

    @Deprecated(
        "Pictures now have an identifying id which should be used. If a random picture is wanted, first retrieve all the ids, select one among them at random then retrieve the picture.",
        ReplaceWith("getPicture(pictureId: Any)")
    )
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
     * A function to retrieve a picture from the database given its id
     *
     * @param category the category of the image to be retrieved
     * @return a CategorizedPicture from the desired category. Null if no picture of the desired
     * category is present in the database.
     * @throws IllegalArgumentException if there is not picture of the provided id in the database
     */
    suspend fun getPicture(pictureId: Id): CategorizedPicture?

    /**
     * A function to retrieve the ids of all the pictures from the database given a category
     *
     * @param category the category of image to be retrieved
     * @return a List of ids. Can be empty if no pictures where found.
     * @throws IllegalArgumentException if the provided category is not present in the database
     */
    suspend fun getPictureIds(category: Category): List<Id>

    /**
     * Retrieves the picture associated to the specified category as its representative picture
     *
     * @param categoryId - the id of the category whose representative picture we want to retrieve
     * @return the representative picture as a categorizedPicture, can be null
     */
    suspend fun getRepresentativePicture(categoryId: Id): CategorizedPicture?

    /**
     * Adds a representative picture to the category. If there is already a representative picture assigned it will be overwritten.
     *
     * @param picture - the picture to put as a representative
     * @param category - the category whose representative picture we want to change
     * @return the previous representative picture, null if there was none
     * @throws IllegalArgumentException if the underlying database does not contain the specified category
     */
    suspend fun putRepresentativePicture(picture: android.net.Uri, category: Category)

    /**
     * Sets a categorized picture as the representative picture of the category it is assigned to,
     * removing it from the pictures of the category in the process.
     *
     * @param picture - the categorized picture to set as representative picture
     * @throws IllegalArgumentException if the underlying database does not contain the specified picture
     */
    suspend fun putRepresentativePicture(picture: CategorizedPicture)

    /**
     * A function that allows to put a picture in the underlying database
     *
     * @param picture the picture to put in the underlying database
     * @param category the category to which the picture belongs
     * @return a Categorized picture built using 'picture' and 'category'
     */
    suspend fun putPicture(picture: android.net.Uri, category: Category): CategorizedPicture

    /**
     * A function to retrieve a category from the underlying database
     *
     * @param categoryId the id of the desired category
     * @return the desired category if present, null otherwise
     */
    suspend fun getCategoryById(categoryId: Id): Category?

    /**
     * A function to retrieve a category from the underlying database
     *
     * @param categoryName the name of the desired category
     * @return the categories of the specified name, can be empty
     */
    suspend fun getCategoryByName(categoryName: String): Collection<Category>

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
     */
    suspend fun putDataset(name: String, categories: Set<Category>): Dataset

    /**
     * Gets a dataset from the underlying database
     *
     * @param id - the name of the desired dataset
     * @return the dataset
     */
    suspend fun getDatasetById(id: Id): Dataset?

    /**
     * Get the dataset of the specified name from the underlying database
     *
     * @param name - the name of the desired dataset
     * @return all the matching datasets
     */
    suspend fun getDatasetByName(datasetName: String): Collection<Dataset>

    /**
     * Deletes the specified dataset from the underlying database
     *
     * @param id - the name of the dataset to delete
     * @throws IllegalArgumentException if there is no dataset of the specified id in the underlying database
     */
    suspend fun deleteDataset(id: Id)

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
    suspend fun getDatasetIds(): Set<Id>

    /**
     * Remove the category from a dataset
     *
     * @param dataset - the dataset from which to remove the category
     * @param category - the category to remove from the dataset
     * @return the dataset with the category removed
     * @throws IllegalArgumentException if the database does not contain the specified category
     */
    suspend fun removeCategoryFromDataset(dataset: Dataset, category: Category): Dataset

    /**
     * Changes the name of a dataset
     *
     * @param dataset - the dataset whose name to change
     * @param newName - the new name the dataset should take
     * @return the dataset with its name changed
     */
    suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset

    /**
     * Adds the category to the dataset, does nothing if the dataset already contains the category.
     *
     * @param dataset - the dataset where the category should be put
     * @param category - the category to add
     * @return the dataset with the new category added
     * @throws IllegalArgumentException if the database does not contain the specified dataset
     * @throws IllegalArgumentException if the database does not contain the specified category
     */
    suspend fun addCategoryToDataset(dataset: Dataset, category: Category): Dataset
}
