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
        // Defensive copy: getStringSet() returns a reference to SharedPreferences' internal set.
        // Mutating it in place then calling putStringSet() with the same reference causes
        // SharedPreferences to skip the disk write (old and new values are the same object).
        set = preferences.getStringSet(STRING_SET_KEY, emptySet())?.toMutableSet() ?: mutableSetOf()
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
        preferences.edit(commit = true) { putStringSet(STRING_SET_KEY, set.toSet()) }
    }

    companion object {
        private const val STRING_SET_KEY = "PersistedSetValues"
    }
}
