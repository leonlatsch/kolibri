package dev.leonlatsch.olivia.database;

import dev.leonlatsch.olivia.database.model.Contact;
import dev.leonlatsch.olivia.rest.dto.MessageDTO;
import dev.leonlatsch.olivia.rest.dto.UserDTO;
import dev.leonlatsch.olivia.database.model.Message;
import dev.leonlatsch.olivia.database.model.User;

/**
 * Class for mapping dto to model and the other way
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class DatabaseMapper {

    private static DatabaseMapper databaseMapper;

    private DatabaseMapper() {}

    public User toModel(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setUid(dto.getUid());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setProfilePicTn(dto.getProfilePicTn());
        return user;
    }

    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setUid(user.getUid());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());

        return dto;
    }

    public Message toModel(MessageDTO dto) {
        if (dto == null) {
            return null;
        }

        Message message = new Message();
        message.setMid(dto.getMid());
        message.setCid(dto.getCid());
        message.setFrom(dto.getFrom());
        message.setTo(dto.getTo());
        message.setType(dto.getType());
        message.setTimestamp(dto.getTimestamp());
        message.setContent(dto.getContent());
        return message;
    }

    public MessageDTO toDto(Message message) {
        if (message == null) {
            return null;
        }

        MessageDTO dto = new MessageDTO();
        dto.setMid(message.getMid());
        dto.setCid(message.getCid());
        dto.setFrom(message.getFrom());
        dto.setTo(message.getTo());
        dto.setType(message.getType());
        dto.setTimestamp(message.getTimestamp());
        dto.setContent(message.getContent());
        return dto;
    }

    public Contact toContact(UserDTO userDTO) {
        return toContact(toModel(userDTO));
    }

    public Contact toContact(User user) {
        if (user == null) {
            return null;
        }

        Contact contact = new Contact();
        contact.setUid(user.getUid());
        contact.setUsername(user.getUsername());
        contact.setProfilePicTn(user.getProfilePicTn());
        return contact;
    }

    public User toUser(Contact contact) {
        if (contact == null) {
            return null;
        }

        User user = new User();
        user.setUid(contact.getUid());
        user.setUsername(contact.getUsername());
        user.setProfilePicTn(contact.getProfilePicTn());
        return user;
    }


    public static DatabaseMapper getInstance() {
        if (databaseMapper == null) {
            databaseMapper = new DatabaseMapper();
        }

        return databaseMapper;
    }
}
