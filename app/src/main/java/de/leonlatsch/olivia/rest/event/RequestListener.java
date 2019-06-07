package de.leonlatsch.olivia.rest.event;

import retrofit2.Response;

public interface RequestListener {

    void requestSucceeded(Response response);

    void requestFailed(Throwable throwable);
}
