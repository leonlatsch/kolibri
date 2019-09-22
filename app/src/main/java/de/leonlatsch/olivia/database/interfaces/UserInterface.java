package de.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Select;

import java.util.List;

import de.leonlatsch.olivia.database.DatabaseMapper;
import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.entity.User;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  Child of {@link BaseInterface}
 *  Syncs the database table 'user' and the Entity {@link User}
 */
public class UserInterface extends BaseInterface<User> {

    private static UserInterface userInterface; // Singleton

    private UserService userService = RestServiceFactory.getUserService();
    private Callback<UserDTO> callback;


    private UserInterface() {
        // Prevent non private instantiation
        setModel(getUser());

        callback = new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    saveUser(response.body());
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {}
        };
    }

    public void loadUser() {
        List<User> list = new Select().from(User.class).execute();
        if (list.size() <= 1) {
            if (list.size() == 1) {
                setModel(list.get(0));
            } else {
                setModel(null);
            }
        } else {
            throw new RuntimeException("more than one user in database");
        }
    }

    public User getUser() {
        if (getModel() == null) {
            loadUser();
        }
        return getModel();
    }

    public void saveUserFromBackend(int uid) {
        Call<UserDTO> call = userService.get(uid);
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
        saveUser(user);
    }

    public void saveUser(User user) {
        if (user != null) {
            if (getModel() != null) {
                deleteUser(getModel());
            }
            user.save();
            notifyListeners(user);
            loadUser();
        }
    }

    public void deleteUser(User user) {
        user.delete();
        setModel(null);
        notifyListeners(null);
    }



    public static UserInterface getInstance() {
        if (userInterface == null) {
            userInterface = new UserInterface();
        }

        return userInterface;
    }
}
