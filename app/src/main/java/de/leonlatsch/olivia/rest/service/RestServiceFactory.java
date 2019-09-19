package de.leonlatsch.olivia.rest.service;

import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.rest.http.OliviaHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestServiceFactory {


    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Values.API_BASE_URL)
            .client(OliviaHttpClient.getOliviaHttpClient())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private static UserService userService;
    private static AuthService authService;

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
}
