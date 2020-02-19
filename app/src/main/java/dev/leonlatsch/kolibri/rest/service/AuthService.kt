package dev.leonlatsch.kolibri.rest.service

import dev.leonlatsch.kolibri.rest.dto.Container
import dev.leonlatsch.kolibri.rest.dto.UserDTO
import dev.leonlatsch.kolibri.rest.http.Headers
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * This service is used for registration and logging in to the backend.
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
interface AuthService {

    /**
     * Login to the kolibri backend with a [UserDTO] containing username and password hash
     *
     * @param dto
     * @return A [Container] with the access token
     */
    @POST("api/v1/auth/login")
    fun login(@Body dto: UserDTO): Call<Container<String>>

    /**
     * Register a new user with a public key
     *
     * @param dto
     * @param publicKey
     * @return A [Container] with the access token
     */
    @PUT("api/v1/auth/register")
    fun register(@Body dto: UserDTO, @Header(Headers.PUBLIC_KEY) publicKey: String): Call<Container<String>>
}
