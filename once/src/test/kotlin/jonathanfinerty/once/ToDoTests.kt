package jonathanfinerty.once

import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ToDoTests {
    @Before
    fun setup() {
        Once.initialise(ApplicationProvider.getApplicationContext())
    }

    @After
    fun cleanUp() {
        Once.clearAll()
    }

    @Test
    fun todo() {
        val task1 = "todo task"
        assertFalse(Once.needToDo(task1))
        assertFalse(Once.beenDone(task1))
        Once.toDo(task1)
        assertTrue(Once.needToDo(task1))
        assertFalse(Once.beenDone(task1))
        Once.markDone(task1)
        assertFalse(Once.needToDo(task1))
        assertTrue(Once.beenDone(task1))
        assertTrue(Once.beenDone(TimeUnit.SECONDS, 1, task1))
    }

    @Test
    fun repeatingToDos() {
        val tag = "repeating to do task"
        Once.toDo(tag)
        assertTrue(Once.needToDo(tag))
        Once.markDone(tag)
        Once.toDo(tag)
        assertTrue(Once.needToDo(tag))
    }

    @Test
    fun todoThisSession() {
        val tag = "to do this session task"
        Once.toDo(Once.THIS_APP_SESSION, tag)
        assertTrue(Once.needToDo(tag))
        assertFalse(Once.beenDone(tag))
        Once.markDone(tag)
        assertFalse(Once.needToDo(tag))
        assertTrue(Once.beenDone(tag))
        Once.toDo(Once.THIS_APP_SESSION, tag)
        assertFalse(Once.needToDo(tag))
        Once.toDo(tag)
        assertTrue(Once.needToDo(tag))
    }

    @Test
    fun todoThisInstall() {
        val tag = "to do this install task"
        Once.toDo(Once.THIS_APP_INSTALL, tag)
        assertTrue(Once.needToDo(tag))
        assertFalse(Once.beenDone(tag))
        Once.markDone(tag)
        assertFalse(Once.needToDo(tag))
        assertTrue(Once.beenDone(tag))
        Once.toDo(Once.THIS_APP_INSTALL, tag)
        assertFalse(Once.needToDo(tag))
        Once.toDo(tag)
        assertTrue(Once.needToDo(tag))
    }

    @Test
    fun todoThisAppVersion() {
        val tag = "todo this app version task"
        Once.toDo(Once.THIS_APP_VERSION, tag)
        assertTrue(Once.needToDo(tag))
        assertFalse(Once.beenDone(tag))
        Once.markDone(tag)
        assertFalse(Once.needToDo(tag))
        assertTrue(Once.beenDone(tag))
        Once.toDo(Once.THIS_APP_VERSION, tag)
        assertFalse(Once.needToDo(tag))
        TestUtils.simulateAppUpdate()
        Once.toDo(Once.THIS_APP_VERSION, tag)
        assertTrue(Once.needToDo(tag))
        Once.toDo(tag)
        assertTrue(Once.needToDo(tag))
    }
}
