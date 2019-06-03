package de.leonlatsch.olivia.dto;

public class UserDTO extends BaseDTO {

    private int uid;
    private String username;
    private String email;
    private String passwordHash;
    private String profilePic;

    public UserDTO() {}

    public UserDTO(int uid, String username, String email, String passwordHash, String profilePic) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.profilePic = profilePic;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
