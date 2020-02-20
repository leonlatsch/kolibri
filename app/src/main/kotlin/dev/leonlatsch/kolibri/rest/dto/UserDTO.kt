package dev.leonlatsch.kolibri.rest.dto

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
class UserDTO {

    var uid: String? = null
    var username: String? = null
    var email: String? = null
    var password: String? = null
    var profilePic: String? = null
    var profilePicTn: String? = null

    constructor()

    constructor(uid: String, username: String, email: String, password: String, profilePic: String, profilePicTn: String) {
        this.uid = uid
        this.username = username
        this.email = email
        this.password = password
        this.profilePic = profilePic
        this.profilePicTn = profilePicTn
    }
}
