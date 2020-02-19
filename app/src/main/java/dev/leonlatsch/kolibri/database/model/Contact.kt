package dev.leonlatsch.kolibri.database.model

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
@Table(name = "contact")
class Contact : Model {

    @Column(name = "uid", index = true)
    var uid: String? = null

    @Column(name = "username")
    var username: String? = null

    @Column(name = "profile_pic_tn")
    var profilePicTn: String? = null

    @Column(name = "public_key")
    var publicKey: String? = null

    constructor() {}

    constructor(uid: String?, username: String?, profilePicTn: String?, publicKey: String?) {
        this.uid = uid
        this.username = username
        this.profilePicTn = profilePicTn
        this.publicKey = publicKey
    }
}
