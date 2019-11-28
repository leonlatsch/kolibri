package dev.leonlatsch.olivia.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    private static final String FILE_NAME = "olivia-preferences";
    private static final int MODE = Context.MODE_PRIVATE;

    public static final String KEY_BACKEND_HTTP_BASEURL = "olivia.backend.http.baseurl";
    public static final String KEY_BACKEND_BROKER_PORT = "olivia.backend.broker.port";
    public static final String KEY_BACKEND_BROKER_HOST = "olivia.backend.broker.host";
    public static final String KEY_APP_SEND_WITH_ENTER = "olivia.app.send_with_enter";
    public static final String KEY_APP_LANGUAGE = "olivia.app.language";

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(FILE_NAME, MODE);
    }

    private Config() {}
}
