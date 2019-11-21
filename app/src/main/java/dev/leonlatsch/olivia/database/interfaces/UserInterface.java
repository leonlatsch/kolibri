package dev.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Select;

import java.util.List;

import dev.leonlatsch.olivia.rest.dto.UserDTO;
import dev.leonlatsch.olivia.database.model.User;

/**
 *  Child of {@link CacheInterface}
 *  Syncs the database table 'user' and the Entity {@link User}
 */
public class UserInterface extends CacheInterface<User> {

    private static UserInterface userInterface; // Singleton

    private UserInterface() {
        setModel(getUser());
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
            throw new RuntimeException("more than one user in database"); // Should never happen case
        }
    }

    public User getUser() {
        if (getModel() == null) {
            loadUser();
        }
        return getModel();
    }

    public String getAccessToken() {
        if (getModel() == null) {
            loadUser();
        }

        return getModel().getAccessToken();
    }

    public void save(UserDTO userDto, String accessToken) {
        User user = getDatabaseMapper().toModel(userDto);
        user.setAccessToken(accessToken);
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
