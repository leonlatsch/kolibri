package de.leonlatsch.olivia.rest.service;

import de.leonlatsch.olivia.dto.StringDTO;
import de.leonlatsch.olivia.dto.UserAuthDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("auth/login")
    Call<StringDTO> login(@Body UserAuthDTO dto);

    @POST("auth/register")
    Call<StringDTO> register(@Body UserAuthDTO dto);
}
