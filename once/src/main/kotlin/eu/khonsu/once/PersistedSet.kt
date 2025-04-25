package eu.khonsu.once

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

internal class PersistedSet(
    context: Context,
    setName: String,
) {
    private val preferences: SharedPreferences
    private val set: MutableSet<String>

    init {
        val preferencesName = "PersistedSet$setName"
        preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        set = preferences.getStringSet(STRING_SET_KEY, mutableSetOf()) ?: mutableSetOf()
    }

    fun put(tag: String) {
        set.add(tag)
        updatePreferences()
    }

    operator fun contains(tag: String): Boolean = set.contains(tag)

    fun remove(tag: String) {
        set.remove(tag)
        updatePreferences()
    }

    fun clear() {
        set.clear()
        updatePreferences()
    }

    private fun updatePreferences() {
        preferences.edit { putStringSet(STRING_SET_KEY, set) }
    }

    companion object {
        private const val STRING_SET_KEY = "PersistedSetValues"
    }
}
