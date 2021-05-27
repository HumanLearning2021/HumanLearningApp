package com.github.HumanLearning2021.HumanLearningApp.model

import com.google.firebase.auth.FirebaseUser

/**
 * An interface representing the part of the model interacting with data sets
 */
interface DatabaseService {

    data class NotFoundException(val id: Id) : Exception("$id not found in database")

    @Deprecated(
        "Pictures now have an identifying id which should be used. If a random picture is wanted, first retrieve all the ids, select one among them at random then retrieve the picture.",
    )
    /**
     * A function to retrieve a picture from the database given a category
     *
     * @param category the category of the image to be retrieved
     * @return a CategorizedPicture from the desired category. Null if no picture of the desired
     * category is present in the database.
     * @throws DatabaseService.NotFoundException if the provided category is not present in the database
     */
    suspend fun getPicture(category: Category): CategorizedPicture?

    /**
     * A function to retrieve a picture from the database given its id
     *
     * @param pictureId the id the image to be retrieved
     * @return the desired image, null if it does not exist in the database
     */
    suspend fun getPicture(pictureId: Id): CategorizedPicture?

    /**
     * A function to retrieve the ids of all the pictures from the database given a category
     *
     * @param category the category of image to be retrieved
     * @return a List of ids. Can be empty if no pictures where found.
     * @throws NotFoundException if the provided category is not present in the database
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
     * @throws NotFoundException if the database does not contain the specified category
     */
    suspend fun putRepresentativePicture(picture: android.net.Uri, category: Category)
    suspend fun putRepresentativePicture(picture: CategorizedPicture)

    /**
     * A function that allows to put a picture in the database
     *
     * @param picture the picture to put in the database
     * @param category the category to which the picture belongs
     * @return a Categorized picture built using 'picture' and 'category'
     * @throws NotFoundException if the category provided is not present in the database
     */
    suspend fun putPicture(picture: android.net.Uri, category: Category): CategorizedPicture

    /**
     * A function to retrieve a category from the database
     *
     * @param id the id of the desired category
     * @return the desired category if present, null otherwise
     */
    suspend fun getCategory(id: Id): Category?

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
     * @throws NotFoundException if the database does not contain the specified category
     */
    suspend fun getAllPictures(category: Category): Set<CategorizedPicture>

    /**
     * Remove the category from the database and from all the datasets contained in this database and using this category
     *
     * @param category - the category to remove from the database
     * @throws NotFoundException if the database does not contain the specified category
     */
    suspend fun removeCategory(category: Category)

    /**
     * Removes the corresponding picture from the database
     *
     * @param picture - the picture to remove from the database
     * @throws NotFoundException if the database does not contain the specified picture
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
     * @param id - the id of the desired dataset
     * @return the dataset
     */
    suspend fun getDataset(id: Id): Dataset?

    /**
     * Deletes the specified dataset from the database
     *
     * @param id - the name of the dataset to delete
     * @throws NotFoundException if there is no dataset of the specified id in the database
     */
    suspend fun deleteDataset(id: Id)

    /**
     * Retrieves all of the available datasets
     *
     * @return a set containing all off the available datasets
     */
    suspend fun getDatasets(): Set<Dataset>

    /**
     * Remove the category from a dataset
     *
     * @param dataset - the dataset from which to remove the category
     * @param category - the category to remove from the dataset
     * @return the dataset with the category removed
     * @throws NotFoundException if the database does not contain the specified category
     * @throws NotFoundException if the database does not contain the specified dataset
     */
    suspend fun removeCategoryFromDataset(dataset: Dataset, category: Category): Dataset

    /**
     * Changes the name of a dataset
     *
     * @param dataset - the dataset whose name to change
     * @param newName - the new name the dataset should take
     * @return the dataset with its name changed
     * @throws NotFoundException if the database does not contain the specified dataset
     */
    suspend fun editDatasetName(dataset: Dataset, newName: String): Dataset

    /**
     * Adds the category to the dataset, does nothing if the dataset already contains the category.
     *
     * @param dataset - the dataset where the category should be put
     * @param category - the category to add
     * @return the dataset with the new category added
     * @throws NotFoundException if the database does not contain the specified dataset
     * @throws NotFoundException if the database does not contain the specified category
     */
    suspend fun addCategoryToDataset(dataset: Dataset, category: Category): Dataset

    suspend fun updateUser(firebaseUser: FirebaseUser): User

    suspend fun setAdminAccess(firebaseUser: FirebaseUser, adminAccess: Boolean): User

    suspend fun checkIsAdmin(user: User): Boolean

    suspend fun getUser(type: User.Type, uid: String): User?

    /**
     * Retrieve the [Statistic] entity associated with the given keys, if any.
     */
    suspend fun getStatistic(userId: User.Id, datasetId: DatasetId): Statistic?

    /**
     * Store the given statistic in the database, overriding any conflicting ones.
     */
    suspend fun putStatistic(statistic: Statistic)
}