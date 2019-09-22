package de.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Select;

import java.util.List;

import de.leonlatsch.olivia.database.DatabaseMapper;
import de.leonlatsch.olivia.dto.Container;
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
    private Callback<Container<UserDTO>> callback;


    private UserInterface() {
        // Prevent non private instantiation
        setModel(getUser());

        callback = new Callback<Container<UserDTO>>() {
            @Override
            public void onResponse(Call<Container<UserDTO>> call, Response<Container<UserDTO>> response) {
                if (response.isSuccessful()) {
                    save(response.body().getContent());
                }
            }

            @Override
            public void onFailure(Call<Container<UserDTO>> call, Throwable t) {}
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

    public void saveUserFromBackend(String accessToken) {
        Call<Container<UserDTO>> call = userService.get(accessToken);
        call.enqueue(callback);
    }

    public void save(UserDTO userDto) {
        User user = DatabaseMapper.mapToEntity(userDto);
        save(user);
    }

    @Override
    public void save(User user) {
        super.save(user);
        loadUser();
    }

    public static UserInterface getInstance() {
        if (userInterface == null) {
            userInterface = new UserInterface();
        }

        return userInterface;
    }
}
