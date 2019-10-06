package de.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Select;

import java.util.List;

import de.leonlatsch.olivia.database.DatabaseMapper;
import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.entity.User;

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
            throw new RuntimeException("more than one user in database");
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

    public void save(UserDTO userDto, String accessToken, String privateKey) {
        User user = DatabaseMapper.mapToEntity(userDto);
        user.setAccessToken(accessToken);
        user.setPrivateKey(privateKey);
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
