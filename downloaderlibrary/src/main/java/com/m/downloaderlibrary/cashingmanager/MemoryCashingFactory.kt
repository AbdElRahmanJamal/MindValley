package com.m.downloaderlibrary.cashingmanager

import android.util.LruCache

object MemoryCashingFactory {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val maxCacheSize = maxMemory / 8
    val lruCache = LruCache<String, Any>(maxCacheSize)

    fun isDownloadedDataFoundInCashingManager(key: String): Boolean {
        return lruCache.get(key) != null
    }

    fun getDownloadedDataFromCash(key: String): Any {
        return lruCache.get(key)
    }

    fun putDownloadedDataIntoCash(key: String, data: Any) {
        kotlin.runCatching {
            if (!isDownloadedDataFoundInCashingManager(key))
                lruCache.put(key, data)
        }.onFailure {
            it.printStackTrace()
        }

    }

    fun clearCashedData(): Boolean {
        runCatching { lruCache.evictAll() }
            .onSuccess { return true }
        return false
    }

}