package de.leonlatsch.olivia.database.interfaces;

import com.activeandroid.query.Select;

import java.util.List;

import de.leonlatsch.olivia.entity.AccessToken;

public class AccessTokenInterface extends BaseInterface<AccessToken> {

    private static AccessTokenInterface accessTokenInterface; // Singleton

    private AccessTokenInterface() {
        setModel(getAccessToken());
    }

    public void loadAccessToken() {
        List<AccessToken> list = new Select().from(AccessToken.class).execute();
        if (list.size() <= 1) {
            if (list.size() == 1) {
                setModel(list.get(0));
            } else {
                setModel(null);
            }
        } else {
            throw new RuntimeException("more than one access token in database");
        }
    }

    public AccessToken getAccessToken() {
        if (getModel() == null) {
            loadAccessToken();
        }

        return getModel();
    }

    @Override
    public void save(AccessToken accessToken) {
        super.save(accessToken);
        loadAccessToken();
    }

    public void save(String accessToken) {
        save(new AccessToken(accessToken));
    }

    public static AccessTokenInterface getInstance() {
        if (accessTokenInterface == null) {
            accessTokenInterface = new AccessTokenInterface();
        }

        return accessTokenInterface;
    }
}
