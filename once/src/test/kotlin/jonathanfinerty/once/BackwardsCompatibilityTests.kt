package jonathanfinerty.once

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class BackwardsCompatibilityTests {
    @Test
    fun backwardsCompatibilityWithPre1Versions() {
        val tag = "version 0.5 tag"
        val applicationContext = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences =
            applicationContext.getSharedPreferences(
                PersistedMap::class.java.getSimpleName() + "TagLastSeenMap",
                Context.MODE_PRIVATE,
            )
        sharedPreferences.edit().putLong(tag, 1234L).apply()
        Once.initialise(applicationContext)
        Once.markDone(tag)
        Assert.assertTrue(Once.beenDone(tag, Amount.exactly(2)))
    }
}
