package dev.leonlatsch.kolibri.boot.jobs;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import dev.leonlatsch.kolibri.boot.BootActivity;
import dev.leonlatsch.kolibri.boot.jobs.base.AsyncJob;
import dev.leonlatsch.kolibri.boot.jobs.base.AsyncJobCallback;
import dev.leonlatsch.kolibri.boot.jobs.base.JobResult;
import dev.leonlatsch.kolibri.constants.Responses;
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface;
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface;
import dev.leonlatsch.kolibri.database.interfaces.UserInterface;
import dev.leonlatsch.kolibri.database.model.User;
import dev.leonlatsch.kolibri.rest.dto.Container;
import dev.leonlatsch.kolibri.rest.dto.UserDTO;
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory;
import dev.leonlatsch.kolibri.rest.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Async job to check and update the logged in user
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class CheckUserAsyncJob extends AsyncJob {

    private UserService userService;
    private UserInterface userInterface;
    private ContactInterface contactInterface;
    private ChatInterface chatInterface;

    public CheckUserAsyncJob(Context context) {
        super(context);
        userInterface = UserInterface.getInstance();
        userService = RestServiceFactory.getUserService();
        contactInterface = ContactInterface.getInstance();
        chatInterface = ChatInterface.getInstance();
    }

    @Override
    public void execute(AsyncJobCallback asyncJobCallback) {
        run(() -> {
            userInterface.loadUser();

            User savedUser = userInterface.getUser();

            if (savedUser != null) {
                asyncJobCallback.onResult(new JobResult<Void>(true, null));

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
                            new Handler(getContext().getMainLooper()).post(() -> {
                                Intent intent = new Intent(getContext(), BootActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                getContext().startActivity(intent);
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Container<UserDTO>> call, Throwable t) {

                    }
                });
            } else {
                asyncJobCallback.onResult(new JobResult<Void>(false, null));
            }
        });
    }
}
