package dev.leonlatsch.olivia.rest.service;

import dev.leonlatsch.olivia.rest.dto.Container;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CommonService {

    @GET("healthcheck")
    Call<Container<Void>> healthcheck();

    @GET("version")
    Call<String> getVersion();

    @GET("broker-port")
    Call<Integer> getBrokerPort();
}
