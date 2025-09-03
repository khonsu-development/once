package eu.khonsu.once

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.concurrent.ConcurrentHashMap

internal class PersistedMap(
    context: Context,
    mapName: String,
) {
    private val preferences: SharedPreferences
    private val map: MutableMap<String, ArrayList<Long>> = ConcurrentHashMap()

    init {
        val preferencesName = "PersistedMap$mapName"
        preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val allPreferences = preferences.all
        for (key in allPreferences.keys) {
            val values: ArrayList<Long> =
                try {
                    stringToList(preferences.getString(key, null))
                } catch (exception: ClassCastException) {
                    loadFromLegacyStorageFormat(key)
                }
            map[key] = values
        }
    }

    private fun loadFromLegacyStorageFormat(key: String): ArrayList<Long> {
        val value = preferences.getLong(key, -1)
        val values: ArrayList<Long> = ArrayList(1)
        values.add(value)
        preferences.edit(commit = true) { putString(key, listToString(values)) }
        return values
    }

    operator fun get(tag: String): List<Long> {
        val longs = map[tag] ?: return emptyList()
        return ArrayList(longs)
    }

    @Synchronized
    fun put(
        tag: String,
        timeSeen: Long,
    ) {
        var lastSeenTimeStamps = map[tag]
        if (lastSeenTimeStamps == null) {
            lastSeenTimeStamps = ArrayList(1)
        }
        lastSeenTimeStamps.add(timeSeen)
        map[tag] = lastSeenTimeStamps
        preferences.edit(commit = true) { putString(tag, listToString(lastSeenTimeStamps)) }
    }

    fun remove(tag: String) {
        map.remove(tag)
        preferences.edit(commit = true) { remove(tag) }
    }

    fun clear() {
        map.clear()
        preferences.edit(commit = true) { clear() }
    }

    private fun listToString(list: List<Long>): String {
        val stringBuilder = StringBuilder()
        var loopDelimiter = ""
        for (l in list) {
            stringBuilder.append(loopDelimiter)
            stringBuilder.append(l)
            loopDelimiter = DELIMITER
        }
        return stringBuilder.toString()
    }

    private fun stringToList(stringList: String?): ArrayList<Long> {
        if (stringList.isNullOrEmpty()) {
            return arrayListOf()
        }
        val strings =
            stringList
                .split(DELIMITER.toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val list: ArrayList<Long> = ArrayList(strings.size)
        for (stringLong in strings) {
            list.add(stringLong.toLong())
        }
        return list
    }

    companion object {
        private const val DELIMITER = ","
    }
}
