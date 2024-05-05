package eu.khonsu.once

import android.content.Context
import android.content.pm.PackageInfo
import androidx.test.core.app.ApplicationProvider
import org.robolectric.Shadows
import java.util.Date

internal object TestUtils {
    fun simulateAppUpdate() {
        val applicationContext = ApplicationProvider.getApplicationContext<Context>()
        val spm = Shadows.shadowOf(applicationContext.packageManager)
        val packageInfo = PackageInfo()
        packageInfo.packageName = applicationContext.packageName
        packageInfo.lastUpdateTime = Date().time
        spm.installPackage(packageInfo)
        Once.initialise(applicationContext)
    }
}
