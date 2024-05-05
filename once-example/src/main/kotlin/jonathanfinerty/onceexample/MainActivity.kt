package jonathanfinerty.onceexample

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import jonathanfinerty.once.Amount.Companion.exactly
import jonathanfinerty.once.Once
import jonathanfinerty.once.Once.Companion.beenDone
import jonathanfinerty.once.Once.Companion.clearAll
import jonathanfinerty.once.Once.Companion.clearDone
import jonathanfinerty.once.Once.Companion.markDone
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        markDone("Application Launched")
        setContentView(R.layout.activity_main)
        val oncePerSessionButton = findViewById<Button>(R.id.once_per_session_button)
        oncePerSessionButton.setOnClickListener {
            if (!beenDone(Once.THIS_APP_SESSION, SHOW_NEW_SESSION_DIALOG)) {
                showDialog("This dialog should only appear once per app session")
                markDone(SHOW_NEW_SESSION_DIALOG)
            }
        }
        val oncePerInstallButton = findViewById<Button>(R.id.once_per_install_button)
        oncePerInstallButton.setOnClickListener {
            if (!beenDone(Once.THIS_APP_INSTALL, SHOW_FRESH_INSTALL_DIALOG)) {
                showDialog("This dialog should only appear once per app installation")
                markDone(SHOW_FRESH_INSTALL_DIALOG)
            }
        }
        val oncePerVersionButton = findViewById<Button>(R.id.once_per_version_button)
        oncePerVersionButton.setOnClickListener {
            if (!beenDone(Once.THIS_APP_VERSION, SHOW_NEW_VERSION_DIALOG)) {
                showDialog("This dialog should only appear once per app version")
                markDone(SHOW_NEW_VERSION_DIALOG)
            }
        }
        val oncePerMinuteButton = findViewById<Button>(R.id.once_per_minute_button)
        oncePerMinuteButton.setOnClickListener {
            if (!beenDone(TimeUnit.MINUTES, 1, SHOW_MINUTE_DIALOG)) {
                showDialog("This dialog should only appear once per minute")
                markDone(SHOW_MINUTE_DIALOG)
            }
        }
        val oncePerSecondButton = findViewById<Button>(R.id.once_per_second_button)
        oncePerSecondButton.setOnClickListener {
            if (!beenDone(1000L, SHOW_SECOND_DIALOG)) {
                showDialog("This dialog should only appear once per second")
                markDone(SHOW_SECOND_DIALOG)
            }
        }
        val oncePerThreePressesButton = findViewById<Button>(R.id.three_presses_button)
        oncePerThreePressesButton.setOnClickListener {
            val buttonPressedTag = "button pressed"
            markDone(buttonPressedTag)
            if (beenDone(buttonPressedTag, exactly(3))) {
                showDialog("This dialog should only appear once every three presses")
                clearDone(buttonPressedTag)
            }
        }
        val resetButton = findViewById<Button>(R.id.reset_all_button)
        resetButton.setOnClickListener { clearAll() }
    }

    private fun showDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL,
            "OK",
        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        alertDialog.show()
    }

    companion object {
        private const val SHOW_NEW_SESSION_DIALOG = "NewSessionDialog"
        private const val SHOW_FRESH_INSTALL_DIALOG = "FreshInstallDialog"
        private const val SHOW_NEW_VERSION_DIALOG = "NewVersionDialog"
        private const val SHOW_MINUTE_DIALOG = "OncePerMinuteDialog"
        private const val SHOW_SECOND_DIALOG = "OncePerSecondDialog"
    }
}
