package de.leonlatsch.olivia.rest.service;

import java.util.List;

import de.leonlatsch.olivia.dto.ProfilePicDTO;
import de.leonlatsch.olivia.dto.StringDTO;
import de.leonlatsch.olivia.dto.UserAuthDTO;
import de.leonlatsch.olivia.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @GET("users")
    Call<List<UserDTO>> getAll();

    @GET("users/getByUid/{uid}")
    Call<UserDTO> getbyUid(@Path("uid") int uid);

    @GET("users/getByEmail/{email}")
    Call<UserDTO> getByEmail(@Path("email") String email);

    @GET("users/getByUsername/{username}")
    Call<UserDTO> getByUsername(@Path("username") String username);

    @GET("users/search/{username}")
    Call<List<UserDTO>> search(@Path("username") String username);

    @POST("users/register")
    Call<StringDTO> create(@Body UserDTO user);

    @PUT("users/update")
    Call<StringDTO> update(@Body UserDTO user);

    @DELETE("users/delete/{uid}")
    Call<StringDTO> delete(@Path("uid") int uid);

    @GET("users/checkUsername/{username}")
    Call<StringDTO> checkUsername(@Path("username") String username);

    @GET("users/checkEmail/{email}")
    Call<StringDTO> checkEmail(@Path("email") String email);

    @POST("users/auth")
    Call<StringDTO> auth(@Body UserAuthDTO authenticator);

    @GET("users/getProfilePic/{uid}")
    Call<ProfilePicDTO> loadProfilePic(@Path("uid") int uid);
}
