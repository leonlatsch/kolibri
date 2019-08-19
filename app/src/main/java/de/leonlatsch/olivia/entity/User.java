package de.leonlatsch.olivia.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "user")
public class User extends Model {

    @Column(name = "uid", index = true)
    private int uid;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "profile_pic_tn")
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

    public String getProfilePicTn() {
        return profilePicTn;
    }

    public void setProfilePicTn(String profilePic) {
        this.profilePicTn = profilePic;
    }
}
