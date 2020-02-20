package dev.leonlatsch.kolibri.rest.service

import android.content.Context
import dev.leonlatsch.kolibri.rest.http.OliviaHttpClient
import dev.leonlatsch.kolibri.settings.Config.KEY_BACKEND_HTTP_BASEURL
import dev.leonlatsch.kolibri.settings.Config.getSharedPreferences
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
object RestServiceFactory {

    private var BASE_URL: String? = null // The base url for the rest api

    private var retrofit: Retrofit? = null

    // Singleton services
    private var userService: UserService? = null
    private var authService: AuthService? = null
    private var chatService: ChatService? = null
    private var configService: ConfigService? = null
    private var commonService: CommonService? = null

    /**
     * Initialize the factory from shared preferences
     *
     * @param context
     */
    fun initialize(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        BASE_URL = sharedPreferences.getString(KEY_BACKEND_HTTP_BASEURL, null)

        provideRetrofit()
        recreateServices()
    }

    /**
     * Initialize the factory with a url
     *
     * @param baseUrl
     */
    fun initialize(baseUrl: String) {
        BASE_URL = baseUrl
        provideRetrofit()
        recreateServices()
    }

    private fun recreateServices() { //TODO: find other solution for this
        if (userService != null) {
            userService = retrofit!!.create(UserService::class.java)
        }

        if (authService != null) {
            authService = retrofit!!.create(AuthService::class.java)
        }

        if (chatService != null) {
            chatService = retrofit!!.create(ChatService::class.java)
        }

        if (commonService != null) {
            commonService = retrofit!!.create(CommonService::class.java)
        }

        if (configService == null) {
            configService = retrofit!!.create(ConfigService::class.java)
        }
    }

    // Constrict the Retrofit object
    private fun provideRetrofit() {
        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL!!)
                .client(OliviaHttpClient.oliviaHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
    }

    fun getUserService(): UserService {
        if (userService == null) {
            userService = retrofit!!.create(UserService::class.java)
        }
        return userService!!
    }

    fun getAuthService(): AuthService {
        if (authService == null) {
            authService = retrofit!!.create(AuthService::class.java)
        }
        return authService!!
    }

    fun getChatService(): ChatService {
        if (chatService == null) {
            chatService = retrofit!!.create(ChatService::class.java)
        }
        return chatService!!
    }

    fun getCommonService(): CommonService {
        if (commonService == null) {
            commonService = retrofit!!.create(CommonService::class.java)
        }

        return commonService!!
    }

    fun getConfigService(): ConfigService {
        if (configService == null) {
            configService = retrofit!!.create(ConfigService::class.java)
        }

        return configService!!
    }
}
