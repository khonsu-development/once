package eu.khonsu.onceexample

import android.app.Application
import eu.khonsu.once.Once.Companion.initialise

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initialise(this)
    }
}
