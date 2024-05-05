package jonathanfinerty.once

import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class OnceTests {
    @Before
    fun setup() {
        Once.initialise(ApplicationProvider.getApplicationContext())
    }

    @After
    fun cleanUp() {
        Once.clearAll()
    }

    @Test
    fun unseenTags() {
        Once.clearAll()
        val seenThisSession = Once.beenDone(Once.THIS_APP_SESSION, TAG_UNDER_TEST)
        assertFalse(seenThisSession)
        val seenThisInstall = Once.beenDone(Once.THIS_APP_INSTALL, TAG_UNDER_TEST)
        assertFalse(seenThisInstall)
        val seenThisAppVersion = Once.beenDone(Once.THIS_APP_VERSION, TAG_UNDER_TEST)
        assertFalse(seenThisAppVersion)
        val seenInTheLastDay = Once.beenDone(TimeUnit.DAYS, 1, TAG_UNDER_TEST)
        assertFalse(seenInTheLastDay)
    }

    @Test
    fun seenTagImmediately() {
        Once.markDone(TAG_UNDER_TEST)
        val seenThisSession = Once.beenDone(Once.THIS_APP_SESSION, TAG_UNDER_TEST)
        assertTrue(seenThisSession)
        val seenThisInstall = Once.beenDone(Once.THIS_APP_INSTALL, TAG_UNDER_TEST)
        assertTrue(seenThisInstall)
        val seenThisAppVersion = Once.beenDone(Once.THIS_APP_VERSION, TAG_UNDER_TEST)
        assertTrue(seenThisAppVersion)
        val seenThisMinute = Once.beenDone(TimeUnit.MINUTES, 1, TAG_UNDER_TEST)
        assertTrue(seenThisMinute)
    }

    @Test
    fun removeFromDone() {
        Once.markDone(TAG_UNDER_TEST)
        Once.clearDone(TAG_UNDER_TEST)
        val seenThisSession = Once.beenDone(Once.THIS_APP_SESSION, TAG_UNDER_TEST)
        assertFalse(seenThisSession)
        val seenThisInstall = Once.beenDone(Once.THIS_APP_INSTALL, TAG_UNDER_TEST)
        assertFalse(seenThisInstall)
        val seenThisAppVersion = Once.beenDone(Once.THIS_APP_VERSION, TAG_UNDER_TEST)
        assertFalse(seenThisAppVersion)
        val seenInTheLastDay = Once.beenDone(TimeUnit.DAYS, 1, TAG_UNDER_TEST)
        assertFalse(seenInTheLastDay)
    }

    @Test
    fun seenTagAfterAppUpdate() {
        Once.markDone(TAG_UNDER_TEST)
        TestUtils.simulateAppUpdate()
        val seenThisSession = Once.beenDone(Once.THIS_APP_SESSION, TAG_UNDER_TEST)
        assertTrue(seenThisSession)
        val seenThisInstall = Once.beenDone(Once.THIS_APP_INSTALL, TAG_UNDER_TEST)
        assertTrue(seenThisInstall)
        val seenThisAppVersion = Once.beenDone(Once.THIS_APP_VERSION, TAG_UNDER_TEST)
        assertFalse(seenThisAppVersion)
        val seenThisMinute = Once.beenDone(TimeUnit.MINUTES, 1, TAG_UNDER_TEST)
        assertTrue(seenThisMinute)
    }

    @Test
    @Throws(InterruptedException::class)
    fun seenTagAfterSecond() {
        Once.markDone(TAG_UNDER_TEST)
        val seenThisSession = Once.beenDone(Once.THIS_APP_SESSION, TAG_UNDER_TEST)
        assertTrue(seenThisSession)
        val seenThisInstall = Once.beenDone(Once.THIS_APP_INSTALL, TAG_UNDER_TEST)
        assertTrue(seenThisInstall)
        val seenThisAppVersion = Once.beenDone(Once.THIS_APP_VERSION, TAG_UNDER_TEST)
        assertTrue(seenThisAppVersion)
        Thread.sleep(TimeUnit.SECONDS.toMillis(1) + 1)
        val seenThisSecond = Once.beenDone(TimeUnit.SECONDS, 1, TAG_UNDER_TEST)
        assertFalse(seenThisSecond)
        val secondInMillis: Long = 1000
        val seenThisSecondInMillis = Once.beenDone(secondInMillis, TAG_UNDER_TEST)
        assertFalse(seenThisSecondInMillis)
    }

    @Test
    fun clearAll() {
        val tag1 = "tag1"
        val tag2 = "tag2"
        Once.markDone(tag1)
        Once.markDone(tag2)
        Once.clearAll()
        assertFalse(Once.beenDone(Once.THIS_APP_SESSION, tag1))
        assertFalse(Once.beenDone(Once.THIS_APP_INSTALL, tag1))
        assertFalse(Once.beenDone(Once.THIS_APP_VERSION, tag1))
        assertFalse(Once.beenDone(1000L, tag1))
        assertFalse(Once.beenDone(Once.THIS_APP_SESSION, tag2))
        assertFalse(Once.beenDone(Once.THIS_APP_INSTALL, tag2))
        assertFalse(Once.beenDone(Once.THIS_APP_VERSION, tag2))
        assertFalse(Once.beenDone(1000L, tag2))
    }

    @Test
    fun emptyTag() {
        val emptyTag = ""
        assertFalse(Once.beenDone(emptyTag))
        Once.markDone(emptyTag)
        assertTrue(Once.beenDone(emptyTag))
    }

    @Test
    fun beenDoneMultipleTimes() {
        val testTag = "action done several times"
        Once.markDone(testTag)
        Once.markDone(testTag)
        assertFalse(Once.beenDone(testTag, Amount.exactly(3)))
        Once.markDone(testTag)
        assertTrue(Once.beenDone(testTag, Amount.exactly(3)))
    }

    @Test
    @Throws(InterruptedException::class)
    fun beenDoneMultipleTimesAcrossScopes() {
        val testTag = "action done several times in different scopes"
        Once.markDone(testTag)
        TestUtils.simulateAppUpdate()
        Thread.sleep(TimeUnit.SECONDS.toMillis(1))
        Once.markDone(testTag)
        assertTrue(Once.beenDone(Once.THIS_APP_INSTALL, testTag, Amount.exactly(2)))
        assertFalse(Once.beenDone(Once.THIS_APP_VERSION, testTag, Amount.exactly(2)))
        Once.markDone(testTag)
        assertTrue(Once.beenDone(Once.THIS_APP_INSTALL, testTag, Amount.exactly(3)))
        assertTrue(Once.beenDone(Once.THIS_APP_VERSION, testTag, Amount.exactly(2)))
    }

    @Test
    fun beenDoneDifferentTimeChecks() {
        val testTag = "test tag"
        Once.markDone(testTag)
        Once.markDone(testTag)
        Once.markDone(testTag)
        assertTrue(Once.beenDone(testTag, Amount.moreThan(-1)))
        assertTrue(Once.beenDone(testTag, Amount.moreThan(2)))
        assertFalse(Once.beenDone(testTag, Amount.moreThan(3)))
        assertTrue(Once.beenDone(testTag, Amount.lessThan(10)))
        assertTrue(Once.beenDone(testTag, Amount.lessThan(4)))
        assertFalse(Once.beenDone(testTag, Amount.lessThan(3)))
    }

    @Test
    @Throws(InterruptedException::class)
    fun beenDoneMultipleTimesWithTimeStamps() {
        Once.markDone(TAG_UNDER_TEST)
        Thread.sleep(TimeUnit.SECONDS.toMillis(1))
        Once.markDone(TAG_UNDER_TEST)
        assertTrue(Once.beenDone(TimeUnit.SECONDS, 3, TAG_UNDER_TEST, Amount.exactly(2)))
        assertTrue(Once.beenDone(TimeUnit.SECONDS, 1, TAG_UNDER_TEST, Amount.exactly(1)))
    }

    @Test
    fun lastDoneWhenNeverDone() {
        val lastDoneDate = Once.lastDone(TAG_UNDER_TEST)
        assertNull(lastDoneDate)
    }

    @Test
    fun lastDone() {
        Once.markDone(TAG_UNDER_TEST)
        val expectedDate = Date()
        val lastDoneDate = Once.lastDone(TAG_UNDER_TEST)
        assertTrue(lastDoneDate!!.time - expectedDate.time < 10)
    }

    @Test
    @Throws(InterruptedException::class)
    fun lastDoneMultipleDates() {
        Once.markDone(TAG_UNDER_TEST)
        Thread.sleep(100)
        Once.markDone(TAG_UNDER_TEST)
        val expectedDate = Date()
        val lastDoneDate = Once.lastDone(TAG_UNDER_TEST)
        assertTrue(lastDoneDate!!.time - expectedDate.time < 10)
    }

    companion object {
        private const val TAG_UNDER_TEST = "testTag"
    }
}
