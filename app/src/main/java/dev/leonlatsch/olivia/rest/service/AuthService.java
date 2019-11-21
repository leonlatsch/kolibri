package dev.leonlatsch.olivia.rest.service;

import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.dto.UserDTO;
import dev.leonlatsch.olivia.rest.http.Headers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthService {

    @POST("auth/login")
    Call<Container<String>> login(@Body UserDTO dto);

    @POST("auth/register")
    Call<Container<String>> register(@Body UserDTO dto, @Header(Headers.PUBLIC_KEY) String publicKey);
}
