package de.leonlatsch.olivia.rest.service;

import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.rest.http.OliviaHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestServiceFactory {


    private static Retrofit retrofit;

    private static UserService userService;
    private static AuthService authService;

    private static Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Values.API_BASE_URL)
                .client(OliviaHttpClient.getOliviaHttpClient())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public static <S> S createService(Class<S> serviceClass) {
        if (retrofit == null) {
            retrofit = provideRetrofit();
        }
        return retrofit.create(serviceClass);
    }
}
