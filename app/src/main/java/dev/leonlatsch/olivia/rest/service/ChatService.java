package dev.leonlatsch.olivia.rest.service;

import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.dto.MessageDTO;
import dev.leonlatsch.olivia.rest.http.Headers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ChatService {

    @POST("chat/send")
    Call<Container<String>> send(@Header(Headers.ACCESS_TOKEN) String accessToken, @Body MessageDTO message);
}
