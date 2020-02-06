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
    @GET("api/v1/healthcheck")
    Call<Container<Void>> healthcheck();

    /**
     * Get the version of the connected backend
     *
     * @return A {@link Container} with the version number
     */
    @GET("api/v1/version")
    Call<String> getVersion();
}
