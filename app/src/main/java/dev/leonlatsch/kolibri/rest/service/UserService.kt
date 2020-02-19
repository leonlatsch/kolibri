package dev.leonlatsch.kolibri.rest.service

import dev.leonlatsch.kolibri.rest.dto.Container
import dev.leonlatsch.kolibri.rest.dto.UserDTO
import dev.leonlatsch.kolibri.rest.http.Headers
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

/**
 * This service is used for all user related requests
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
interface UserService {

    /**
     * Get a your own user
     *
     * @param accessToken
     * @return A [Container] with your user
     */
    @GET("api/v1/user/get")
    operator fun get(@Header(Headers.ACCESS_TOKEN) accessToken: String): Call<Container<UserDTO>>

    /**
     * Get a user with a uid
     *
     * @param accessToken
     * @param uid         The uid of the user
     * @return A [Container] with the specified user
     */
    @GET("api/v1/user/get/{uid}")
    operator fun get(@Header(Headers.ACCESS_TOKEN) accessToken: String, @Path("uid") uid: String): Call<Container<UserDTO>>

    /**
     * Search for users with a username
     *
     * @param accessToken
     * @param username    The query for the search
     * @return A [Container] with a [List] of users, matching the sarched username
     */
    @GET("api/v1/user/search/{username}")
    fun search(@Header(Headers.ACCESS_TOKEN) accessToken: String, @Path("username") username: String): Call<Container<List<UserDTO>>>

    /**
     * Update your user
     *
     * @param accessToken
     * @param user        The user [UserDTO] filled with the fields to update
     * @return A empty [Container]
     */
    @PATCH("api/v1/user/update")
    fun update(@Header(Headers.ACCESS_TOKEN) accessToken: String, @Body user: UserDTO): Call<Container<String>>

    /**
     * Delete your user in the backend
     *
     * @param accessToken
     * @return A empty [Container]
     */
    @DELETE("api/v1/user/delete")
    fun delete(@Header(Headers.ACCESS_TOKEN) accessToken: String): Call<Container<String>>

    /**
     * Check if a username is already taken
     * Possible responses can be found in [dev.leonlatsch.kolibri.constants.Responses]
     *
     * @param accessToken
     * @param username
     * @return A [Container] with a message indicating if the username ss free
     */
    @GET("api/v1/user/check/username/{username}")
    fun checkUsername(@Header(Headers.ACCESS_TOKEN) accessToken: String, @Path("username") username: String): Call<Container<String>>

    /**
     * Check if a email address is already taken
     * Possible responses can be found in [dev.leonlatsch.kolibri.constants.Responses]
     *
     * @param accessToken
     * @param email
     * @return A [Container] with a message indicating if the email address is free
     */
    @GET("api/v1/user/check/email/{email}")
    fun checkEmail(@Header(Headers.ACCESS_TOKEN) accessToken: String, @Path("email") email: String): Call<Container<String>>

    /**
     * Load the full profile picture for a user
     *
     * @param accessToken
     * @param uid
     * @return A [Container] with a base64 String with the full profile picture
     */
    @GET("api/v1/user/get/profile-pic/{uid}")
    fun loadProfilePic(@Header(Headers.ACCESS_TOKEN) accessToken: String, @Path("uid") uid: String): Call<Container<String>>

    /**
     * Get the public key for a user
     *
     * @param accessToken
     * @param uid
     * @return A [Container] with a base64 encoded public key
     */
    @GET("api/v1/user/public-key/get/{uid}")
    fun getPublicKey(@Header(Headers.ACCESS_TOKEN) accessToken: String, @Path("uid") uid: String): Call<Container<String>>

    /**
     * Update your own public key in the backend
     *
     * @param accessToken
     * @param publicKey
     * @return A empty [Container]
     */
    @PATCH("api/v1/user/public-key/update")
    fun updatePublicKey(@Header(Headers.ACCESS_TOKEN) accessToken: String, @Header(Headers.PUBLIC_KEY) publicKey: String): Call<Container<String>>
}
