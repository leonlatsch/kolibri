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

    @GET("user/get")
    Call<UserDTO> get();

    @GET("user/search/top100/{username}")
    Call<List<UserDTO>> search(@Path("username") String username);

    @GET("user/search/{username}")
    Call<List<UserDTO>> searchAll(@Path("username") String username);

    @PUT("user/update")
    Call<StringDTO> update(@Body UserDTO user);

    @DELETE("user/delete")
    Call<StringDTO> delete();

    @GET("user/check/username/{username}")
    Call<StringDTO> checkUsername(@Path("username") String username);

    @GET("user/check/email/{email}")
    Call<StringDTO> checkEmail(@Path("email") String email);

    @GET("user/get/profilePic")
    Call<ProfilePicDTO> loadProfilePic();

    @GET("user/public-key/get/{uid}")
    Call<StringDTO> getPublicKey(@Path("uid") int uid);

    @PUT("user/public-key/update")
    Call<StringDTO> updatePublicKey(); // String publicKey ??
}
