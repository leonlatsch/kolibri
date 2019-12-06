package dev.leonlatsch.olivia.rest.service;

import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.dto.UserDTO;
import dev.leonlatsch.olivia.rest.http.Headers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * This service is used for registration and logging in to the backend.
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * Login to the olivia backend with a {@link UserDTO} containing email and password hash
     *
     * @param dto
     * @return A {@link Container} with the access token
     */
    @POST("auth/login")
    Call<Container<String>> login(@Body UserDTO dto);

    /**
     * Register a new user with a public key
     *
     * @param dto
     * @param publicKey
     * @return A {@link Container} with the access token
     */
    @POST("auth/register")
    Call<Container<String>> register(@Body UserDTO dto, @Header(Headers.PUBLIC_KEY) String publicKey);
}
