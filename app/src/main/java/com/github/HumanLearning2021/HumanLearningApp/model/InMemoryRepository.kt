package com.github.HumanLearning2021.HumanLearningApp.model

import android.util.Base64
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

/**
 * In-memory, impermanent key-value storage.
 */
class InMemoryRepository<V>(
    private val random: Random = Random,
) : Repository<String, V> {

    private inner class Context {
        var data = mutableMapOf<String, V>()
    }

    private val mutex = Mutex()
    private val ctx = Context()
    private suspend fun <R> withContext(action: Context.() -> R): R = mutex.withLock {
        ctx.action()
    }

    suspend fun createWith(fkv: (String) -> V) = withContext {
        var k: String
        do {
            k = Base64.encodeToString(random.nextBytes(15), Base64.URL_SAFE)
        } while (k in data)
        data[k] = fkv(k)
        k
    }

    override suspend fun updateWith(k: String, f: (V?) -> V?) = withContext {
        f(data[k])?.let { data[k] = it }
        Unit
    }

    override suspend fun getById(k: String) = withContext {
        data[k]
    }

    override suspend fun delete(k: String): Boolean = withContext { data.remove(k) != null }

    override suspend fun getIds() = withContext { data.keys }

    override suspend fun updateAll(f: (V) -> V) = withContext {
        for ((k, v) in data) {
            data[k] = f(v)
        }
    }

    override suspend fun update(k: String, v: V) = withContext {
        data[k] = v
    }

    override suspend fun create(v: V): String = createWith { v }
}
