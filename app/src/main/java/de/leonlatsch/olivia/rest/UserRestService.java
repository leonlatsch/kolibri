package de.leonlatsch.olivia.rest;

import java.util.List;

import de.leonlatsch.olivia.transfer.TransferUser;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UserRestService {

    @GET("users")
    Call<List<TransferUser>> getAllUsers();
}
