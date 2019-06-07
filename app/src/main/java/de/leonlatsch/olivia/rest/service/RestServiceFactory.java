package de.leonlatsch.olivia.rest.service;

import de.leonlatsch.olivia.rest.http.OliviaHttpClient;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestServiceFactory {

    private static final String BASE_URL = "https://olivia.leonlatsch.de:7443/";

    // TODO: create a server side stored keypair and REMOVE this afterwords
    private static final String API_TOKEN = "bfdc99b120cd49e0e1a18dc8267afa3e";
    private static final String API_KEY = "6eb77586c6fbfd1280412db3bf0e103f";

	//TODO: create a service with a repository that cas just be used without creating a thread o.ä.
    public static UserService createUserService() {
        OkHttpClient client = OliviaHttpClient.getOliviaHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(UserService.class);
    }


}
