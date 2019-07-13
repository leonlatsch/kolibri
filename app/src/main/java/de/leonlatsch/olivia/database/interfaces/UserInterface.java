package de.leonlatsch.olivia.database.interfaces;

import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.entity.User;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import de.leonlatsch.olivia.database.DatabaseMapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInterface {

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

    public void loadUser(int uid) {
        Call<UserDTO> call = userService.getbyUid(uid);
        call.enqueue(callback);
    }

    public void loadUser(String username) {
        Call<UserDTO> call = userService.getByUsername(username);
        call.enqueue(callback);
    }

    public void loadUser(String email, boolean isEmail) {
        if (!isEmail) {
            loadUser(email);
        } else {
            Call<UserDTO> call = userService.getByEmail(email);
            call.enqueue(callback);
        }
    }

    public static UserInterface getInstance() {
        if (userInterface == null) {
            userInterface = new UserInterface();
        }

        return userInterface;
    }
}
