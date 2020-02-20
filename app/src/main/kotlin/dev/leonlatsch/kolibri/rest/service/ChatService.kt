package dev.leonlatsch.kolibri.rest.service

import dev.leonlatsch.kolibri.rest.dto.Container
import dev.leonlatsch.kolibri.rest.dto.MessageDTO
import dev.leonlatsch.kolibri.rest.http.Headers
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT

/**
 * This service is used for sending messages to the backend.
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
interface ChatService {

    /**
     * Send a message to the backend which gets later pushed to the queue
     *
     * @param accessToken The access token of teh sending user
     * @param message     The [MessageDTO] to be sent
     * @return A empty [Container]
     */
    @PUT("api/v1/chat/send")
    fun send(@Header(Headers.ACCESS_TOKEN) accessToken: String, @Body message: MessageDTO): Call<Container<String>>
}
