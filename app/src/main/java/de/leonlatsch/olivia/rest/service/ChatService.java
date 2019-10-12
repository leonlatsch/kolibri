package de.leonlatsch.olivia.rest.service;

import de.leonlatsch.olivia.rest.dto.Container;
import de.leonlatsch.olivia.rest.dto.MessageDTO;
import de.leonlatsch.olivia.rest.http.Headers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ChatService {

    @POST("chat/send")
    Call<Container<String>> send(@Header(Headers.ACCESS_TOKEN) String accessToken, @Body MessageDTO message);
}
