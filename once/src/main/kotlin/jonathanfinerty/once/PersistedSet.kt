package jonathanfinerty.once

import android.content.Context
import android.content.SharedPreferences

internal class PersistedSet(context: Context, setName: String) {
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

    operator fun contains(tag: String): Boolean {
        return set.contains(tag)
    }

    fun remove(tag: String) {
        set.remove(tag)
        updatePreferences()
    }

    fun clear() {
        set.clear()
        updatePreferences()
    }

    private fun updatePreferences() {
        val edit = preferences.edit()
        edit.putStringSet(STRING_SET_KEY, set)
        edit.apply()
    }

    companion object {
        private const val STRING_SET_KEY = "PersistedSetValues"
    }
}
