package dev.leonlatsch.olivia.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    private static final String FILE_NAME = "olivia-preferences";
    private static final int MODE = Context.MODE_PRIVATE;

    public static final String KEY_BACKEND_HOST = "olivia.backend.host";
    public static final String KEY_BACKEND_HTTP_PORT = "olivia.backend.http.port";
    public static final String KEY_BACKEND_BROKER_PORT = "olivia.backend.broker.port";
    public static final String KEY_LANGUAGE = "olivia.localisation.language";

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(FILE_NAME, MODE);
    }

    private Config() {}
}
