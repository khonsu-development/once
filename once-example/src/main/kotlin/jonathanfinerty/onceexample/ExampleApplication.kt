package jonathanfinerty.onceexample

import android.app.Application
import jonathanfinerty.once.Once.Companion.initialise

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initialise(this)
    }
}
