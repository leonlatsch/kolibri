package dev.leonlatsch.kolibri.rest.service;

import java.util.HashMap;

import dev.leonlatsch.kolibri.rest.dto.Container;
import dev.leonlatsch.kolibri.rest.http.Headers;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

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

    /**
     * Get the remote config as json.
     *
     * @return A {@link Container} with the remote config as json
     */
    @GET("api/v1/config/get")
    Call<Container<HashMap<String, Object>>> get();
}
