package de.leonlatsch.olivia.rest.repository;

import java.util.List;

import de.leonlatsch.olivia.dto.UserAuthDTO;
import de.leonlatsch.olivia.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserRestRepository {

    @GET("users")
    Call<List<UserDTO>> getAll();

    @GET("users/getByUid/{uid}")
    Call<UserDTO> getbyUid(@Path("name") int uid);

    @GET("users(getByEmail/{email}")
    Call<UserDTO> getByEmail(@Path("email") String email);

    @GET("users/getByUsername/{username}")
    Call<UserDTO> getByUsername(@Path("username") String username);

    @POST("users/register")
    Call<String> create(@Body UserDTO user);

    @PUT("users/update")
    Call<String> update(@Body UserDTO user);

    @DELETE("users/delete/{uid}")
    Call<String> delete(@Path("uid") int uid);

    @GET("users/checkUsername/{username}")
    Call<String> checkUsername(@Path("username") String username);

    @GET("users/checkEmail/{email}")
    Call<String> checkEmail(@Path("email") String email);

    @GET("users/auth")
    Call<String> auth(@Body UserAuthDTO authenticator);
}
