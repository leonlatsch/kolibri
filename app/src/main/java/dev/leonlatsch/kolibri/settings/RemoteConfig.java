package dev.leonlatsch.kolibri.settings;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 * Remote Configuration
 *
 * @author Leon Lastch
 * @since 1.0.0
 */
public class RemoteConfig {

    private static final Logger log = LoggerFactory.getLogger(RemoteConfig.class);

    public static final String ENABLE_REGISTRATION = "enableRegistration";

    private static RemoteConfig instance;

    public static RemoteConfig getInstance() {
        if (instance == null) {
            instance = new RemoteConfig();
        }

        return instance;
    }

    private RemoteConfig() {
    }

    private HashMap<String, Object> config = new HashMap<>();
    private ObjectMapper mapper = new ObjectMapper();

    public void setConfig(HashMap<String, Object> config) {
        this.config = config;
    }

    public String getString(String key, String defaultValue) {
        Object value = config.get(key);
        if (value instanceof String) {
            return (String) value;
        } else {
            return defaultValue;
        }
    }

    public int getInt(String key, int defaultValue) {
        Object value = config.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = config.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return defaultValue;
        }
    }
}
