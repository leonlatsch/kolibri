package dev.leonlatsch.olivia.boot.jobs;

import android.content.Context;

import dev.leonlatsch.olivia.database.interfaces.UserInterface;
import dev.leonlatsch.olivia.rest.service.RestServiceFactory;
import dev.leonlatsch.olivia.rest.service.UserService;

public class UpdateUserJob extends Job {

    private UserService userService;
    private UserInterface userInterface;

    public UpdateUserJob(Context context) {
        super(context);
        userInterface = UserInterface.getInstance();
        userService = RestServiceFactory.getUserService();
    }

    @Override
    void execute(JobResultCallback jobResultCallback) {
        userInterface.loadUser();
    }
}
