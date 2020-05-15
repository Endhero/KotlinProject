package com.lcd.kotlinproject.data.source.local

import android.util.LruCache

class MemorySource {
    companion object{
        var memoryCache = LruCache<String, Any>(100)
    }

    fun put(key: String, s:Any) = memoryCache.put(key, s)

    operator fun get(key: String?) = memoryCache[key]

    fun clear() = memoryCache.evictAll()

    operator fun contains(key: String?)= memoryCache["key"] != null
}