package dev.leonlatsch.kolibri.rest.service

import dev.leonlatsch.kolibri.rest.dto.Container
import retrofit2.Call
import retrofit2.http.GET

/**
 * This service is used for common functions
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
interface CommonService {

    /**
     * Get the version of the connected backend
     *
     * @return A [Container] with the version number
     */
    @GET("api/v1/version")
    fun version(): Call<String>

    /**
     * Make a healthcheck to a backend
     *
     * @return A empty [Container]
     */
    @GET("api/v1/healthcheck")
    fun healthcheck(): Call<Container<Void>>
}
