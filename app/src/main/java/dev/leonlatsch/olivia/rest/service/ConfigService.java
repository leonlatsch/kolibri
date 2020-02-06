package dev.leonlatsch.olivia.rest.service;

import dev.leonlatsch.olivia.rest.dto.Container;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * This service is used to get configuration from the backend
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public interface ConfigService {

    /**
     * Get the port for the running broker
     *
     * @return A {@link Container} with the broker port as an int
     */
    @GET("api/v1/config/broker-port")
    Call<Integer> getBrokerPort();
}
