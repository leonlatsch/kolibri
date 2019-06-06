package de.leonlatsch.olivia.rest.event;

import java.util.Optional;

import retrofit2.Response;

public interface RequestListener {

    void requestSucceeded(Response event);

    void requestFailed(Throwable throwable);
}
