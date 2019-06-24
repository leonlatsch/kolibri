package de.leonlatsch.olivia.rest.service;

import de.leonlatsch.olivia.rest.http.OliviaHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestServiceFactory {

    private static final String BASE_URL = "https://olivia.leonlatsch.de:/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OliviaHttpClient.getOliviaHttpClient())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private static UserService userService;

    public static UserService getUserService() {
        if (userService == null) {
            userService = retrofit.create(UserService.class);
        }

        return userService;
    }
}
