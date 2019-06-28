package de.leonlatsch.olivia.entity;

import com.orm.SugarRecord;

import java.sql.Blob;

public class User extends SugarRecord {

    private int uid;
    private String username;
    private String email;
    private String password;
    private String profilePicTn;

    public User() {}

    public User(int uid, String username, String email, String password, String profilePicTn) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePicTn = profilePicTn;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePic() {
        return profilePicTn;
    }

    public void setProfilePic(String profilePic) {
        this.profilePicTn = profilePic;
    }
}
