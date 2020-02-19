package dev.leonlatsch.kolibri.settings

import android.content.Context
import android.content.SharedPreferences

/**
 * A class that holds config keys and values for shares preferences
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
object Config {

    const val KEY_BACKEND_HTTP_BASEURL = "kolibri.backend.http.baseurl" // The base url for the rest api
    const val KEY_BACKEND_BROKER_PORT = "kolibri.backend.broker.port" // The port for the broker connection
    const val KEY_BACKEND_BROKER_HOST = "kolibri.backend.broker.host" // The host for the broker connection
    const val KEY_APP_SEND_WITH_ENTER = "kolibri.app.send_with_enter" // Send a message with enter
    private const val FILE_NAME = "dev.leonlatsch.kolibri_preferences" // Filename will be kolibri-preferences.xml
    private const val MODE = Context.MODE_PRIVATE // Always open in private mode

    /**
     * Get [SharedPreferences] from the preference file with private access
     *
     * @param context
     * @return
     */
    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(FILE_NAME, MODE)
    }
}
