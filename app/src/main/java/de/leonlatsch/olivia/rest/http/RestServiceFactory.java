package de.leonlatsch.olivia.rest.http;

import de.leonlatsch.olivia.rest.UserRestService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestServiceFactory {

    private static final String BASE_URL = "https://bfdc99b120cd49e0e1a18dc8267afa3e:6eb77586c6fbfd1280412db3bf0e103f@olivia.leonlatsch.de:7443/";

    // TODO: create a server side stored keypair and REMOVE this afterwords
    private static final String API_TOKEN = "bfdc99b120cd49e0e1a18dc8267afa3e";
    private static final String API_KEY = "6eb77586c6fbfd1280412db3bf0e103f";

    public static UserRestService createUserService() {
        OkHttpClient client = Unsafe
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(UserRestService.class);
    }

    private static String getAbsoluteUrl(String url) {
        return BASE_URL + url;
    }
}
