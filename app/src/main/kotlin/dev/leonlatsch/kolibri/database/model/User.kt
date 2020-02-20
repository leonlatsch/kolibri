package dev.leonlatsch.kolibri.database.model

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
@Table(name = "user")
class User : Model {

    @Column(name = "uid", index = true)
    var uid: String? = null

    @Column(name = "username")
    var username: String? = null

    @Column(name = "email")
    var email: String? = null

    @Column(name = "password")
    var password: String? = null

    @Column(name = "profile_pic_tn")
    var profilePicTn: String? = null

    @Column(name = "token")
    var accessToken: String? = null

    constructor() {}

    constructor(uid: String?, username: String?, email: String?, password: String?, profilePicTn: String?, accessToken: String?) {
        this.uid = uid
        this.username = username
        this.email = email
        this.password = password
        this.profilePicTn = profilePicTn
        this.accessToken = accessToken
    }
}
