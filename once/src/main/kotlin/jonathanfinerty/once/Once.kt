package jonathanfinerty.once

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.IntDef
import java.util.Date
import java.util.concurrent.TimeUnit

class Once {
    companion object {
        const val THIS_APP_INSTALL = 0
        const val THIS_APP_VERSION = 1
        const val THIS_APP_SESSION = 2

        private var lastAppUpdatedTime: Long = -1
        private var tagLastSeenMap: PersistedMap? = null
        private var toDoSet: PersistedSet? = null
        private var sessionList: ArrayList<String>? = null

        /**
         * This method needs to be called before Once can be used.
         * Typically it will be called from your Application class's onCreate method.
         *
         * @param context Application context
         */
        @JvmStatic
        fun initialise(context: Context) {
            tagLastSeenMap = PersistedMap(context, "TagLastSeenMap")
            toDoSet = PersistedSet(context, "ToDoSet")
            if (sessionList == null) {
                sessionList = ArrayList()
            }
            val packageManager = context.packageManager
            try {
                val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
                lastAppUpdatedTime = packageInfo.lastUpdateTime
            } catch (ignored: PackageManager.NameNotFoundException) {
            }
        }

        /**
         * Mark a tag as 'to do' within a given scope, if it has already marked to do or been done
         * within that scope then it will not be marked.
         *
         * @param scope The scope to not repeat the to do task in
         * @param tag   A string identifier unique to the operation.
         */
        @JvmStatic
        fun toDo(
            @Scope scope: Int,
            tag: String,
        ) {
            val tagSeenList = tagLastSeenMap!![tag]
            if (tagSeenList.isEmpty()) {
                toDoSet!!.put(tag)
                return
            }
            val tagLastSeen = tagSeenList[tagSeenList.size - 1]
            if (scope == THIS_APP_VERSION && tagLastSeen <= lastAppUpdatedTime) {
                toDoSet!!.put(tag)
            }
        }

        /**
         * Mark a tag as 'to do' regardless of whether or not its ever been marked done before
         *
         * @param tag A string identifier unique to the operation.
         */
        @JvmStatic
        fun toDo(tag: String) {
            toDoSet!!.put(tag)
        }

        /**
         * Checks if a tag is currently marked as 'to do'.
         *
         * @param tag A string identifier unique to the operation.
         * @return `true` if the operation associated with `tag` has been marked 'to do' and has not been passed to `markDone()` since.
         */
        @JvmStatic
        fun needToDo(tag: String): Boolean {
            return toDoSet!!.contains(tag)
        }

        @JvmStatic
        fun lastDone(tag: String): Date? {
            val lastSeenTimeStamps = tagLastSeenMap!![tag]
            if (lastSeenTimeStamps.isEmpty()) {
                return null
            }
            val lastTimestamp = lastSeenTimeStamps[lastSeenTimeStamps.size - 1]
            return Date(lastTimestamp)
        }

        /**
         * Checks if a tag has been marked done, ever.
         *
         * Equivalent of calling `beenDone(int scope, String tag)` with scope of `THIS_APP_INSTALL`.
         *
         * @param tag A string identifier unique to the operation.
         * @return `true` if the operation associated with `tag` has been marked done within
         * the given `scope`.
         */
        @JvmStatic
        fun beenDone(tag: String): Boolean {
            return beenDone(THIS_APP_INSTALL, tag, Amount.moreThan(0))
        }

        /**
         * Check if a tag has been done a specific number of times.
         *
         * @param tag           A string identifier unique to the operation.
         * @param numberOfTimes Requirement for how many times the operation must have be done.
         * @return `true` if the operation associated with `tag` has been marked done the specific `numberOfTimes`.
         */
        @JvmStatic
        fun beenDone(
            tag: String,
            numberOfTimes: CountChecker,
        ): Boolean {
            return beenDone(THIS_APP_INSTALL, tag, numberOfTimes)
        }

        /**
         * Checks if a tag has been marked done within a given scope a specific number of times.
         *
         * @param scope         The scope in which to check whether the tag has been done, either
         * `THIS_APP_INSTALL` or `THIS_APP_VERSION`.
         * @param tag           A string identifier unique to the operation.
         * @param numberOfTimes Requirement for how many times the operation must have be done.
         * @return `true` if the operation associated with `tag` has been marked done within
         * the given `scope` the specific `numberOfTimes`.
         */
        @JvmStatic
        @JvmOverloads
        fun beenDone(
            @Scope scope: Int,
            tag: String,
            numberOfTimes: CountChecker = Amount.moreThan(0),
        ): Boolean {
            val tagSeenDates = tagLastSeenMap!![tag]
            return if (tagSeenDates.isEmpty()) {
                false
            } else {
                when (scope) {
                    THIS_APP_INSTALL -> numberOfTimes.check(tagSeenDates.size)
                    THIS_APP_SESSION -> {
                        var counter = 0
                        val sessionArray = sessionList!!.toTypedArray<String>()
                        for (tagFromList in sessionArray) {
                            if (tagFromList == tag) {
                                counter++
                            }
                        }
                        numberOfTimes.check(counter)
                    }

                    THIS_APP_VERSION -> {
                        var counter = 0
                        for (seenDate in tagSeenDates) {
                            if (seenDate > lastAppUpdatedTime) {
                                counter++
                            }
                        }
                        numberOfTimes.check(counter)
                    }

                    else -> {
                        var counter = 0
                        for (seenDate in tagSeenDates) {
                            if (seenDate > lastAppUpdatedTime) {
                                counter++
                            }
                        }
                        numberOfTimes.check(counter)
                    }
                }
            }
        }

        /**
         * Checks if a tag has been marked done within a given time span a specific number of times. (e.g. twice in the last 5 minutes)
         *
         * @param timeUnit      The units of time to work in.
         * @param amount        The quantity of timeUnit.
         * @param tag           A string identifier unique to the operation.
         * @param numberOfTimes Requirement for how many times the operation must have be done.
         * @return `true` if the operation associated with `tag` has been marked done at least `numberOfTimes`
         * within the last provide time span.
         */
        @JvmStatic
        @JvmOverloads
        fun beenDone(
            timeUnit: TimeUnit,
            amount: Long,
            tag: String,
            numberOfTimes: CountChecker = Amount.moreThan(0),
        ): Boolean {
            val timeInMillis = timeUnit.toMillis(amount)
            return beenDone(timeInMillis, tag, numberOfTimes)
        }

        /**
         * Checks if a tag has been marked done within a the last `timeSpanInMillis` milliseconds
         * a specific number of times.
         *
         * @param timeSpanInMillis How many milliseconds ago to check if a tag has been marked done
         * since.
         * @param tag              A string identifier unique to the operation.
         * @param numberOfTimes    Requirement for how many times the operation must have be done.
         * @return `true` if the operation associated with `tag` has been marked done
         * within the last X milliseconds.
         */
        @JvmStatic
        @JvmOverloads
        fun beenDone(
            timeSpanInMillis: Long,
            tag: String,
            numberOfTimes: CountChecker = Amount.moreThan(0),
        ): Boolean {
            val tagSeenDates = tagLastSeenMap!![tag]
            if (tagSeenDates.isEmpty()) {
                return false
            }
            var counter = 0
            for (seenDate in tagSeenDates) {
                val sinceSinceCheckTime = Date().time - timeSpanInMillis
                if (seenDate > sinceSinceCheckTime) {
                    counter++
                }
            }
            return numberOfTimes.check(counter)
        }

        /**
         * Marks a tag (associated with some operation) as done. The `tag` is marked done at the time
         * of calling this method.
         *
         * @param tag A string identifier unique to the operation.
         */
        @JvmStatic
        fun markDone(tag: String) {
            tagLastSeenMap!!.put(tag, Date().time)
            sessionList!!.add(tag)
            toDoSet!!.remove(tag)
        }

        /**
         * Clears a tag as done. All checks with `beenDone()` with that tag will return `false` until
         * it is marked done again.
         *
         * @param tag A string identifier unique to the operation.
         */
        @JvmStatic
        fun clearDone(tag: String) {
            tagLastSeenMap!!.remove(tag)
            sessionList!!.remove(tag)
        }

        /**
         * Clears a tag as 'to do'. All checks with `needToDo()` with that tag will return `false` until
         * it is marked 'to do' again.
         *
         * @param tag A string identifier unique to the operation.
         */
        @JvmStatic
        fun clearToDo(tag: String) {
            toDoSet!!.remove(tag)
        }

        /**
         * Clears all tags as done. All checks with `beenDone()` with any tag will return `false`
         * until they are marked done again.
         */
        @JvmStatic
        fun clearAll() {
            tagLastSeenMap!!.clear()
            sessionList!!.clear()
        }

        /**
         * Clears all tags as 'to do'. All checks with `needToDo()` with any tag will return `false`
         * until they are marked 'to do' again.
         */
        @JvmStatic
        fun clearAllToDos() {
            toDoSet!!.clear()
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [THIS_APP_INSTALL, THIS_APP_VERSION, THIS_APP_SESSION])
    annotation class Scope
}
