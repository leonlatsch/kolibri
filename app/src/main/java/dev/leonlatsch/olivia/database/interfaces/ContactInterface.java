package dev.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

import dev.leonlatsch.olivia.database.model.Contact;
import dev.leonlatsch.olivia.database.model.User;
import dev.leonlatsch.olivia.rest.dto.UserDTO;

public class ContactInterface extends BaseInterface {

    private static final String QUEUE_UID_WHERE = "uid = ?";

    private static ContactInterface contactInterface; // Singleton


    private ContactInterface() {}

    public Contact getContact(String uid) {
        return new Select().from(Contact.class).where(QUEUE_UID_WHERE, uid).executeSingle();
    }

    public List<Contact> getALl() {
        return new Select().from(Contact.class).execute();
    }

    public void delete(Contact contact) {
        contact.delete();
    }

    public void deleteAll() {
        new Delete().from(Contact.class).execute();
    }

    public String save(UserDTO userDTO, String publicKey) {
        return save(getDatabaseMapper().toModel(userDTO), publicKey);
    }

    public String save(User user, String publicKey) {
        Contact contact = getDatabaseMapper().toContact(user);
        contact.setPublicKey(publicKey);
        contact.setUid(user.getUid());
        return save(contact);
    }

    public String save(Contact contact) {
        Contact saved = new Select().from(Contact.class).where(QUEUE_UID_WHERE, contact.getUid()).executeSingle();

        if (saved != null) {
            saved.delete();
        }

        contact.save();
        return contact.getUid();
    }

    public void updateContact(Contact contact) {
        if (contact.getProfilePicTn() != null) {
            new Update(Contact.class).set("uid = ?, username = ?, profile_pic_tn = ?, public_key = ?",
                    contact.getUid(), contact.getUsername(), contact.getProfilePicTn(), contact.getPublicKey())
                    .where(QUEUE_UID_WHERE, contact.getUid()).execute();
        } else {
            new Update(Contact.class).set("uid = ?, username = ?, public_key = ?",
                    contact.getUid(), contact.getUsername(), contact.getPublicKey())
                    .where(QUEUE_UID_WHERE, contact.getUid()).execute();
        }
    }

    public static ContactInterface getInstance() {
        if (contactInterface == null) {
            contactInterface = new ContactInterface();
        }

        return contactInterface;
    }
}
