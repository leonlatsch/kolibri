package de.leonlatsch.olivia.rest.service;

import java.util.List;

import de.leonlatsch.olivia.dto.Container;
import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.rest.http.Headers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @GET("user/get")
    Call<Container<UserDTO>> get(@Header(Headers.ACCESS_TOKEN) String accessToken);

    @GET("user/search/top100/{username}")
    Call<Container<List<UserDTO>>> search(@Header(Headers.ACCESS_TOKEN) String accessToken, @Path("username") String username);

    @GET("user/search/{username}")
    Call<Container<List<UserDTO>>> searchAll(@Header(Headers.ACCESS_TOKEN) String accessToken, @Path("username") String username);

    @PUT("user/update")
    Call<Container<String>> update(@Header(Headers.ACCESS_TOKEN) String accessToken, @Body UserDTO user);

    @DELETE("user/delete")
    Call<Container<String>> delete(@Header(Headers.ACCESS_TOKEN) String accessToken);

    @GET("user/check/username/{username}")
    Call<Container<String>> checkUsername(@Header(Headers.ACCESS_TOKEN) String accessToken,@Path("username") String username);

    @GET("user/check/email/{email}")
    Call<Container<String>> checkEmail(@Header(Headers.ACCESS_TOKEN) String accessToken, @Path("email") String email);

    @GET("user/get/profile-pic/{uid}")
    Call<Container<String>> loadProfilePic(@Header(Headers.ACCESS_TOKEN) String accessToken, @Path("uid") int uid);

    @GET("user/public-key/get/{uid}")
    Call<Container<String>> getPublicKey(@Header(Headers.ACCESS_TOKEN) String accessToken, @Path("uid") int uid);

    @PUT("user/public-key/update")
    Call<Container<String>> updatePublicKey(@Header(Headers.ACCESS_TOKEN) String accessToken, @Header(Headers.PUBLIC_KEY) String publicKey);
}
