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
}
