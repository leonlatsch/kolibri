package de.leonlatsch.olivia.util;

import com.orm.SugarRecord;

import de.leonlatsch.olivia.dto.BaseDTO;
import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.entity.User;

public class DatabaseMapper {

    public static SugarRecord maptoEntity(BaseDTO dto) {
        if (dto instanceof UserDTO) {
            UserDTO userDTO = (UserDTO) dto;
            User user = new User();
            user.setUid(userDTO.getUid());
            user.setEmail(userDTO.getEmail());
            user.setUsername(userDTO.getUsername());
            user.setProfilePicTn(userDTO.getProfilePicTn());
            return user;
        }

        return null;
    }
}
