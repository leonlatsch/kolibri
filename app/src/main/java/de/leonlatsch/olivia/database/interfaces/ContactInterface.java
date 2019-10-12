package de.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Select;

import de.leonlatsch.olivia.database.model.Contact;
import de.leonlatsch.olivia.database.model.User;
import de.leonlatsch.olivia.rest.dto.Container;
import de.leonlatsch.olivia.rest.dto.UserDTO;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactInterface extends BaseInterface {

    private static final String QUEUE_CONTACT_ID_WHERE = "contact_id = ?";
    private static final String QUEUE_UID_WHERE = "uid = ?";

    private static ContactInterface contactInterface; // Singleton

    private UserService userService;
    private UserInterface userInterface;

    private ContactInterface() {
        userService = RestServiceFactory.getUserService();
        userInterface = UserInterface.getInstance();
    }

    public Contact getContact(int uid) {
        return new Select().from(Contact.class).where(QUEUE_UID_WHERE, uid).executeSingle();
    }

    public Contact getContact(String contactId) {
        return new Select().from(Contact.class).where(QUEUE_CONTACT_ID_WHERE, contactId).executeSingle();
    }

    public void delete(Contact contact) {
        contact.delete();
    }

    public void save(UserDTO userDTO, String publicKey) {
        save(getDatabaseMapper().toModel(userDTO), publicKey);
    }

    public void save(User user, String publicKey) {
        Contact contact = getDatabaseMapper().toContact(user);
        contact.setPublicKey(publicKey);
        save(contact);
    }

    public void save(Contact contact) {
        Contact saved = new Select().from(Contact.class).where(QUEUE_CONTACT_ID_WHERE, contact.getContactId()).executeSingle();

        if (saved != null) {
            saved.delete();
        }

        contact.save();
    }

    public static ContactInterface getInstance() {
        if (contactInterface == null) {
            contactInterface = new ContactInterface();
        }

        return contactInterface;
    }
}
