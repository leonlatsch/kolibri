package dev.leonlatsch.olivia.rest.service;

import android.content.Context;
import android.content.SharedPreferences;

import dev.leonlatsch.olivia.rest.http.OliviaHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static dev.leonlatsch.olivia.settings.Config.KEY_BACKEND_HTTP_BASEURL;
import static dev.leonlatsch.olivia.settings.Config.getSharedPreferences;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
public class RestServiceFactory {

    private static String BASE_URL = null; // The base url for the rest api

    private static Retrofit retrofit;

    // Singleton services
    private static UserService userService;
    private static AuthService authService;
    private static ChatService chatService;
    private static CommonService commonService;

    /**
     * Initialize the factory from shared preferences
     *
     * @param context
     */
    public static void initialize(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        BASE_URL = sharedPreferences.getString(KEY_BACKEND_HTTP_BASEURL, null);

        provideRetrofit();
        recreateServices();
    }

    /**
     * Initialize the factory with a url
     *
     * @param baseUrl
     */
    public static void initialize(String baseUrl) {
        BASE_URL = baseUrl;
        provideRetrofit();
        recreateServices();
    }

    private static void recreateServices() { //TODO: find other solution for this
        if (userService != null) {
            userService = retrofit.create(UserService.class);
        }

        if (authService != null) {
            authService = retrofit.create(AuthService.class);
        }

        if (chatService != null) {
            chatService = retrofit.create(ChatService.class);
        }

        if (commonService != null) {
            commonService = retrofit.create(CommonService.class);
        }
    }

    // Constrict the Retrofit object
    private static void provideRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OliviaHttpClient.getOliviaHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public static UserService getUserService() {
        if (userService == null) {
            userService = retrofit.create(UserService.class);
        }
        return userService;
    }

    public static AuthService getAuthService() {
        if (authService == null) {
            authService = retrofit.create(AuthService.class);
        }
        return authService;
    }

    public static ChatService getChatService() {
        if (chatService == null) {
            chatService = retrofit.create(ChatService.class);
        }
        return chatService;
    }

    public static CommonService getCommonService() {
        if (commonService == null) {
            commonService = retrofit.create(CommonService.class);
        }

        return commonService;
    }
}
