package de.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Select;

import java.util.List;

import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.entity.User;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import de.leonlatsch.olivia.database.DatabaseMapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInterface {

    /**
     * Singleton instance
     */
    private static UserInterface userInterface;

    private UserService userService = RestServiceFactory.getUserService();
    private Callback<UserDTO> callback;


    private UserInterface() {
        // Prevent non private instantiation
        callback = new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    User user = DatabaseMapper.mapToEntity(response.body());
                    user.save();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {}
        };
    }

    public User loadUser() {

        List<User> list = new Select().from(User.class).execute();
        if (list.size() <= 1) {
            if (list.size() == 1) {
                return list.get(0);
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("more than one user in database");
        }
    }

    public void saveUserFromBackend(int uid) {
        Call<UserDTO> call = userService.getbyUid(uid);
        call.enqueue(callback);
    }

    public void saveUserFromBackend(String username) {
        Call<UserDTO> call = userService.getByUsername(username);
        call.enqueue(callback);
    }

    public void saveUserFromBackend(String email, boolean isEmail) {
        if (!isEmail) {
            saveUserFromBackend(email);
        } else {
            Call<UserDTO> call = userService.getByEmail(email);
            call.enqueue(callback);
        }
    }

    public void saveUser(UserDTO userDto) {
        User user = DatabaseMapper.mapToEntity(userDto);
        user.save();
    }

    public void saveUser(User user) {
        user.save();
    }

    public void deleteUser(User user) {
        user.delete();
    }

    public static UserInterface getInstance() {
        if (userInterface == null) {
            userInterface = new UserInterface();
        }

        return userInterface;
    }
}
