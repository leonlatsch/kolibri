package dev.leonlatsch.olivia.rest.service;

import java.util.List;

import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.dto.UserDTO;
import dev.leonlatsch.olivia.rest.http.Headers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

/**
 * This service is used for all user related requests
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public interface UserService {

    /**
     * Get a your own user
     *
     * @param accessToken
     * @return A {@link Container} with your user
     */
    @GET("api/v1/user/get")
    Call<Container<UserDTO>> get(@Header(Headers.ACCESS_TOKEN) String accessToken);

    /**
     * Get a user with a uid
     *
     * @param accessToken
     * @param uid         The uid of the user
     * @return A {@link Container} with the specified user
     */
    @GET("api/v1/user/get/{uid}")
    Call<Container<UserDTO>> get(@Header(Headers.ACCESS_TOKEN) String accessToken, @Path("uid") String uid);

    /**
     * Search for users with a username
     *
     * @param accessToken
     * @param username    The query for the search
     * @return A {@link Container} with a {@link List} of users, matching the sarched username
     */
    @GET("api/v1/user/search/{username}")
    Call<Container<List<UserDTO>>> search(@Header(Headers.ACCESS_TOKEN) String accessToken, @Path("username") String username);

    /**
     * Update your user
     *
     * @param accessToken
     * @param user        The user {@link UserDTO} filled with the fields to update
     * @return A empty {@link Container}
     */
    @PATCH("api/v1/user/update")
    Call<Container<String>> update(@Header(Headers.ACCESS_TOKEN) String accessToken, @Body UserDTO user);

    /**
     * Delete your user in the backend
     *
     * @param accessToken
     * @return A empty {@link Container}
     */
    @DELETE("api/v1/user/delete")
    Call<Container<String>> delete(@Header(Headers.ACCESS_TOKEN) String accessToken);

    /**
     * Check if a username is already taken
     * Possible responses can be found in {@link dev.leonlatsch.olivia.constants.Responses}
     *
     * @param accessToken
     * @param username
     * @return A {@link Container} with a message indicating if the username ss free
     */
    @GET("api/v1/user/check/username/{username}")
    Call<Container<String>> checkUsername(@Header(Headers.ACCESS_TOKEN) String accessToken, @Path("username") String username);

    /**
     * Check if a email address is already taken
     * Possible responses can be found in {@link dev.leonlatsch.olivia.constants.Responses}
     *
     * @param accessToken
     * @param email
     * @return A {@link Container} with a message indicating if the email address is free
     */
    @GET("api/v1/user/check/email/{email}")
    Call<Container<String>> checkEmail(@Header(Headers.ACCESS_TOKEN) String accessToken, @Path("email") String email);

    /**
     * Load the full profile picture for a user
     *
     * @param accessToken
     * @param uid
     * @return A {@link Container} with a base64 String with the full profile picture
     */
    @GET("api/v1/user/get/profile-pic/{uid}")
    Call<Container<String>> loadProfilePic(@Header(Headers.ACCESS_TOKEN) String accessToken, @Path("uid") String uid);

    /**
     * Get the public key for a user
     *
     * @param accessToken
     * @param uid
     * @return A {@link Container} with a base64 encoded public key
     */
    @GET("api/v1/user/public-key/get/{uid}")
    Call<Container<String>> getPublicKey(@Header(Headers.ACCESS_TOKEN) String accessToken, @Path("uid") String uid);

    /**
     * Update your own public key in the backend
     *
     * @param accessToken
     * @param publicKey
     * @return A empty {@link Container}
     */
    @PATCH("api/v1/user/public-key/update")
    Call<Container<String>> updatePublicKey(@Header(Headers.ACCESS_TOKEN) String accessToken, @Header(Headers.PUBLIC_KEY) String publicKey);
}
