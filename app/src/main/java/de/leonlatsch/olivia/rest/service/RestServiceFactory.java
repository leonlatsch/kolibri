package de.leonlatsch.olivia.rest.service;

import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.rest.http.OliviaHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestServiceFactory {


    private static Retrofit retrofit;

    private static UserService userService;
    private static AuthService authService;
    private static ChatService chatService;

    private static void provideRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Values.API_BASE_URL)
                    .client(OliviaHttpClient.getOliviaHttpClient())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
    }

    public static UserService getUserService() {
        provideRetrofit();
        if (userService == null) {
            userService = retrofit.create(UserService.class);
        }
        return userService;
    }

    public static AuthService getAuthService() {
        provideRetrofit();
        if (authService == null) {
            authService = retrofit.create(AuthService.class);
        }
        return authService;
    }

    public static ChatService getChatService() {
        provideRetrofit();
        if (chatService == null) {
            chatService = retrofit.create(ChatService.class);
        }
        return chatService;
    }
}
