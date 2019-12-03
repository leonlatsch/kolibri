package dev.leonlatsch.olivia.boot.jobs;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import dev.leonlatsch.olivia.boot.jobs.base.AsyncJob;
import dev.leonlatsch.olivia.boot.jobs.base.JobResult;
import dev.leonlatsch.olivia.boot.jobs.base.AsyncJobCallback;
import dev.leonlatsch.olivia.broker.MessageConsumer;
import dev.leonlatsch.olivia.database.interfaces.ChatInterface;
import dev.leonlatsch.olivia.database.interfaces.ContactInterface;
import dev.leonlatsch.olivia.database.interfaces.UserInterface;
import dev.leonlatsch.olivia.database.model.Contact;
import dev.leonlatsch.olivia.rest.dto.Container;
import dev.leonlatsch.olivia.rest.dto.UserDTO;
import dev.leonlatsch.olivia.rest.service.RestServiceFactory;
import dev.leonlatsch.olivia.rest.service.UserService;
import retrofit2.Response;

public class UpdateContactsAsyncJob extends AsyncJob {

    private UserInterface userInterface;
    private ContactInterface contactInterface;
    private ChatInterface chatInterface;
    private UserService userService;

    public UpdateContactsAsyncJob(Context context) {
        super(context);
        userInterface = UserInterface.getInstance();
        contactInterface = ContactInterface.getInstance();
        chatInterface = ChatInterface.getInstance();
        userService = RestServiceFactory.getUserService();
    }

    @Override
    public void execute(AsyncJobCallback asyncJobCallback) { //TODO: create backend function to get a list of contacts in one request
        run(() -> {
            List<Contact> contacts = contactInterface.getALl();

            boolean success = true;
            int contactsUpdated = 0;

            for (Contact contact : contacts) {
                try {
                    Response<Container<UserDTO>> contactResponse = userService.get(userInterface.getAccessToken(), contact.getUid()).execute();
                    Response<Container<String>> publicKeyResponse = userService.getPublicKey(userInterface.getAccessToken(), contact.getUid()).execute();

                    if (contactResponse.isSuccessful() && publicKeyResponse.isSuccessful()) {
                        UserDTO userDTO = contactResponse.body().getContent();
                        String publicKey = publicKeyResponse.body().getContent();

                        boolean changed = false;

                        if (contact.getProfilePicTn() == null) {
                            if (userDTO.getProfilePicTn() != null) {
                                contact.setProfilePicTn(userDTO.getProfilePicTn());
                                changed = true;
                            }
                        } else if (!contact.getProfilePicTn().equals(userDTO.getProfilePicTn())) {
                            contact.setProfilePicTn(userDTO.getProfilePicTn());
                            changed = true;
                        }

                        if (!contact.getPublicKey().equals(publicKey)) {
                            contact.setPublicKey(publicKey);
                            changed = true;
                        }

                        if (changed) {
                            contactInterface.updateContact(contact);
                            MessageConsumer.notifyChatListChangedFromExternal(chatInterface.getChatForContact(contact.getUid()));
                            contactsUpdated++;
                        }
                    }
                } catch (IOException e) {
                    success = false;
                }
            }

            if (asyncJobCallback != null) {
                asyncJobCallback.onResult(new JobResult<>(success, contactsUpdated));
            }
        });
    }
}
