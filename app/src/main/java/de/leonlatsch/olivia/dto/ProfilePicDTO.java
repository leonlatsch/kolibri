package de.leonlatsch.olivia.dto;

public class ProfilePicDTO extends BaseDTO {

    private String profilePic;

    public ProfilePicDTO(){}

    public ProfilePicDTO(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
