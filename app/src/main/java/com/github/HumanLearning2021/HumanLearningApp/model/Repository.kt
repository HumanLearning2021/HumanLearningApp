package com.github.HumanLearning2021.HumanLearningApp.model

/**
 * Generic definition of homogenous key-value storage.
 *
 * [Reference](https://medium.com/@pererikbergman/repository-design-pattern-e28c0f3e4a30)
 *
 */
interface Repository<K, V> {
    /**
     * Add an entry to the data, assigning it a new key.
     * @param v the data to store.
     * @return the new key
     */
    suspend fun create(v: V): K

    /**
     * Change the data at a specific key.
     * @param k the key
     * @param v the new value
     */
    suspend fun update(k: K, v: V)

    /**
     * Transform the data stored at a given key
     * @param k the key
     * @param v a pure function to change the stored value
     */
    suspend fun updateWith(k: K, f: (V?) -> V?)

    /**
     * Retrieve the data stored at a given key
     * @param k the key
     * @return the data retrieved, or null if nothing is stored
     */
    suspend fun getById(k: K): V?

    /**
     * Discard the data stored at a given key.
     * @param k the key
     * @return `false` if the nothing was stored, `true` otherwise
     */
    suspend fun delete(k: K): Boolean

    /**
     * Collect all keys where something is stored.
     * @return the set of the keys of all non-empty entries
     */
    suspend fun getIds(): Set<K>

    /**
     * Transform the data stored key-by-key.
     * @param f a pure function to alter the data of one entry.
     */
    suspend fun updateAll(f: (V) -> V)
}

