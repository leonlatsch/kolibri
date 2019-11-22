package dev.leonlatsch.olivia.boot.jobs;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.logging.Handler;

import dev.leonlatsch.olivia.boot.BootActivity;
import dev.leonlatsch.olivia.constants.Responses;
import dev.leonlatsch.olivia.database.interfaces.ChatInterface;
import dev.leonlatsch.olivia.database.interfaces.ContactInterface;
import dev.leonlatsch.olivia.database.interfaces.UserInterface;
import dev.leonlatsch.olivia.database.model.User;
import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.dto.UserDTO;
import dev.leonlatsch.olivia.rest.service.RestServiceFactory;
import dev.leonlatsch.olivia.rest.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckUserJob extends Job {

    private UserService userService;
    private UserInterface userInterface;
    private ContactInterface contactInterface;
    private ChatInterface chatInterface;

    public CheckUserJob(Context context) {
        super(context);
        userInterface = UserInterface.getInstance();
        userService = RestServiceFactory.getUserService();
        contactInterface = ContactInterface.getInstance();
        chatInterface = ChatInterface.getInstance();
    }

    @Override
    public void execute(JobResultCallback jobResultCallback) {
        run(() -> {
            userInterface.loadUser();

            User savedUser = userInterface.getUser();

            if (savedUser != null) {
                jobResultCallback.onResult(new JobResult<Void>(true, null));
            } else {
                jobResultCallback.onResult(new JobResult<Void>(false, null));
            }
        });

        postExecute();
    }

    @Override
    public void postExecute() {
        User savedUser = userInterface.getUser();

        Call<Container<UserDTO>> call = userService.get(userInterface.getAccessToken());
        call.enqueue(new Callback<Container<UserDTO>>() {
            @Override
            public void onResponse(Call<Container<UserDTO>> call, Response<Container<UserDTO>> response) {
                if (response.code() == Responses.CODE_OK) { // Update saved user
                    userInterface.save(response.body().getContent(), savedUser.getAccessToken());
                } else {
                    // if the saved user is not in the backend
                    userInterface.delete(savedUser);
                    contactInterface.deleteAll();
                    chatInterface.deleteAll();
                    getContext().startActivity(new Intent(getContext(), BootActivity.class));
                }
            }

            @Override
            public void onFailure(Call<Container<UserDTO>> call, Throwable t) {

            }
        });
    }
}
