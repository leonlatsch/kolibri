package dev.leonlatsch.kolibri.boot.jobs;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import dev.leonlatsch.kolibri.boot.jobs.base.AsyncJob;
import dev.leonlatsch.kolibri.boot.jobs.base.AsyncJobCallback;
import dev.leonlatsch.kolibri.boot.jobs.base.JobResult;
import dev.leonlatsch.kolibri.broker.MessageConsumer;
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface;
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface;
import dev.leonlatsch.kolibri.database.interfaces.UserInterface;
import dev.leonlatsch.kolibri.database.model.Contact;
import dev.leonlatsch.kolibri.rest.dto.Container;
import dev.leonlatsch.kolibri.rest.dto.UserDTO;
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory;
import dev.leonlatsch.kolibri.rest.service.UserService;
import retrofit2.Response;

/**
 * Async job to update the saved contacts.
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
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
    protected void run(AsyncJobCallback asyncJobCallback) {
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
                        // Notify the chat list if it is already displayed
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
    }
}
