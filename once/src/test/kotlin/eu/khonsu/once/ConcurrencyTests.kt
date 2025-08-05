package eu.khonsu.once

import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

@Config(
    sdk = [28],
    manifest = Config.NONE,
)
@RunWith(RobolectricTestRunner::class)
class ConcurrencyTests {
    @Before
    fun setup() {
        Once.initialise(ApplicationProvider.getApplicationContext())
    }

    @After
    fun cleanUp() {
        Once.clearAll()
    }

    @Test
    @Throws(Throwable::class)
    fun concurrentMarkDone() {
        testConcurrency { Once.markDone("tag under test") }
    }

    @Test
    @Throws(Throwable::class)
    fun concurrentToDo() {
        testConcurrency { Once.toDo("tag under test") }
    }

    @Throws(Throwable::class)
    private fun testConcurrency(functionUnderTest: Runnable) {
        val threadFactory = ExceptionTrackingThreadFactory()
        val exec = Executors.newFixedThreadPool(16, threadFactory)
        (0..9999).forEach { _ ->
            exec.execute(functionUnderTest)
        }
        exec.shutdown()
        exec.awaitTermination(10, TimeUnit.SECONDS)
        if (threadFactory.lastExceptionThrown != null) {
            throw threadFactory.lastExceptionThrown!!
        }
    }

    class ExceptionTrackingThreadFactory : ThreadFactory {
        var lastExceptionThrown: Throwable? = null
            private set

        override fun newThread(runnable: Runnable): Thread {
            val t = Thread(runnable)
            t.setUncaughtExceptionHandler { _: Thread?, throwable: Throwable? ->
                lastExceptionThrown = throwable
            }
            return t
        }
    }
}
