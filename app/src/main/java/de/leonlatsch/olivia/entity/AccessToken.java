package de.leonlatsch.olivia.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "access_token")
public class AccessToken extends Model {

    @Column(name = "token", index = true)
    private String token;

    public AccessToken() {}

    public AccessToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
