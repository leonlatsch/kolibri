package dev.leonlatsch.olivia.rest.service;

import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.dto.MessageDTO;
import dev.leonlatsch.olivia.rest.http.Headers;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * This service is used for sending messages to the backend.
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public interface ChatService {

    /**
     * Send a message to the backend which gets later pushed to the queue
     *
     * @param accessToken The access token of teh sending user
     * @param message     The {@link MessageDTO} to be sent
     * @return A empty {@link Container}
     */
    @POST("chat/send")
    Call<Container<String>> send(@Header(Headers.ACCESS_TOKEN) String accessToken, @Body MessageDTO message);
}
