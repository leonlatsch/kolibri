package de.leonlatsch.olivia.rest.service;

import de.leonlatsch.olivia.dto.Container;
import de.leonlatsch.olivia.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("auth/login")
    Call<Container<String>> login(@Body UserDTO dto);

    @POST("auth/register")
    Call<Container<String>> register(@Body UserDTO dto);
}
