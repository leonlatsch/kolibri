package dev.leonlatsch.olivia.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    private static final String FILE_NAME = "olivia-preferences"; // Filename will be olivia-preferences.xml
    private static final int MODE = Context.MODE_PRIVATE; // Always open in private mode

    public static final String KEY_BACKEND_HTTP_BASEURL = "olivia.backend.http.baseurl"; // The base url for the rest api
    public static final String KEY_BACKEND_BROKER_PORT = "olivia.backend.broker.port"; // The port for the broker connection
    public static final String KEY_BACKEND_BROKER_HOST = "olivia.backend.broker.host"; // The host for the broker connection
    public static final String KEY_APP_SEND_WITH_ENTER = "olivia.app.send_with_enter"; // Send a message with enter
    public static final String KEY_APP_LANGUAGE = "olivia.app.language"; // Manual App Language

    /**
     * Get {@link SharedPreferences} from the preference file with private access
     *
     * @param context
     * @return
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(FILE_NAME, MODE);
    }

    private Config() {}
}
