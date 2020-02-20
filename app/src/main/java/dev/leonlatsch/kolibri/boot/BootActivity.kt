package dev.leonlatsch.kolibri.boot

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.activeandroid.ActiveAndroid
import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.boot.jobs.CheckUserAsyncJob
import dev.leonlatsch.kolibri.boot.jobs.UpdateContactsAsyncJob
import dev.leonlatsch.kolibri.boot.jobs.ValidateBackendJob
import dev.leonlatsch.kolibri.main.MainActivity
import dev.leonlatsch.kolibri.main.login.LoginActivity
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory

/**
 * The first Activity to be started.
 * It runs a bunch of jobs and based of that runs other activities
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class BootActivity : AppCompatActivity() {

    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)

        ActiveAndroid.initialize(this)

        Handler().postDelayed({
            // Delay execution for 100 ms to show splash screen
            val result = ValidateBackendJob(this).execute()
            if (result.isSuccessful()) {
                RestServiceFactory.initialize(this)
                val job = CheckUserAsyncJob(this)
                job.execute({ userResult ->
                    Handler(getApplicationContext().getMainLooper()).post({
                        if (userResult.isSuccessful()) {
                            startActivity(Intent(getApplicationContext(), MainActivity::class.java))
                            UpdateContactsAsyncJob(this).execute(null)
                        } else {
                            startActivity(Intent(getApplicationContext(), LoginActivity::class.java))
                        }
                        finish()
                    })
                })
            } else { // If there is no backend config show the BackendDialog
                val dialog = BackendDialog(this)
                dialog.setOnDismissListener({ dialogInterface ->
                    startActivity(Intent(getApplicationContext(), BootActivity::class.java))
                    finish()
                })
                dialog.show()
            }
        }, 100)
    }
}
