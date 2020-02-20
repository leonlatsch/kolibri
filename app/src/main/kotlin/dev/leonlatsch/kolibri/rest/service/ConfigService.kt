package dev.leonlatsch.kolibri.rest.service

import dev.leonlatsch.kolibri.rest.dto.Container
import retrofit2.Call
import retrofit2.http.GET

/**
 * This service is used to get configuration from the backend
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
interface ConfigService {

    /**
     * Get the port for the running broker
     *
     * @return A [Container] with the broker port as an int
     */
    @GET("api/v1/config/broker-port")
    fun brokerPort(): Call<Int>
}
