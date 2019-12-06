package dev.leonlatsch.olivia.rest.service;

import dev.leonlatsch.olivia.rest.dto.Container;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * This service is used for common functions
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public interface CommonService {

    /**
     * Make a healthcheck to a backend
     *
     * @return A empty {@link Container}
     */
    @GET("healthcheck")
    Call<Container<Void>> healthcheck();

    /**
     * Get the version of the connected backend
     *
     * @return A {@link Container} with the version number
     */
    @GET("version")
    Call<String> getVersion();

    /**
     * Get the port for the running broker
     *
     * @return A {@link Container} with the broker port as an int
     */
    @GET("broker-port")
    Call<Integer> getBrokerPort();
}
