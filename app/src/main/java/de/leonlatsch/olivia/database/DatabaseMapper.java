package de.leonlatsch.olivia.database;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

import de.leonlatsch.olivia.constants.Formats;
import de.leonlatsch.olivia.rest.dto.MessageDTO;
import de.leonlatsch.olivia.rest.dto.UserDTO;
import de.leonlatsch.olivia.database.model.Message;
import de.leonlatsch.olivia.database.model.User;

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
        message.setTimestamp(stringToTimestamp(dto.getTimestamp()));
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
        dto.setTimestamp(message.getTimestamp().toString());
        dto.setContent(message.getContent());
        return dto;
    }

    private Timestamp stringToTimestamp(String timestamp) {
        try {
            Date parsed = Formats.DATE_FORMAT.parse(timestamp);
            return new Timestamp(parsed.getTime());
        } catch (ParseException e) {
            return null;
        }
    }


    public static DatabaseMapper getInstance() {
        if (databaseMapper == null) {
            databaseMapper = new DatabaseMapper();
        }

        return databaseMapper;
    }
}
