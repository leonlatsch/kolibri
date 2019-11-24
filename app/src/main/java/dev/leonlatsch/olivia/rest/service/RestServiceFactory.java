package dev.leonlatsch.olivia.rest.service;

import dev.leonlatsch.olivia.constants.Values;
import dev.leonlatsch.olivia.rest.http.OliviaHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RestServiceFactory {


    private static Retrofit retrofit;

    private static UserService userService;
    private static AuthService authService;
    private static ChatService chatService;
    private static CommonService commonService;

    private static void provideRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Values.API_BASE_URL)
                    .client(OliviaHttpClient.getOliviaHttpClient())
                    .addConverterFactory(ScalarsConverterFactory.create())
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

    public static CommonService getCommonService() {
        provideRetrofit();
        if (commonService == null) {
            commonService = retrofit.create(CommonService.class);
        }

        return commonService;
    }
}
