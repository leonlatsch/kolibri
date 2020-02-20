package dev.leonlatsch.kolibri.database.model

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
@Table(name = "key_pair")
class KeyPair : Model {

    @Column(name = "uid", index = true)
    var uid: String? = null

    @Column(name = "public_key")
    var publicKey: String? = null

    @Column(name = "private_key")
    var privateKey: String? = null

    constructor()

    constructor(uid: String?, publicKey: String?, privateKey: String?) {
        this.uid = uid
        this.publicKey = publicKey
        this.privateKey = privateKey
    }
}
