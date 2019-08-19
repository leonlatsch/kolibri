package de.leonlatsch.olivia.database;

import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.entity.User;

public class DatabaseMapper {

    public static User mapToEntity(UserDTO dto) {
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

    public static UserDTO mapToDTO(User user) {
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
}
