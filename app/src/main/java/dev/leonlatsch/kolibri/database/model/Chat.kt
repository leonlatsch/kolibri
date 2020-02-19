package dev.leonlatsch.kolibri.database.model

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
@Table(name = "chat")
class Chat : Model {

    @Column(name = "cid", index = true)
    var cid: String? = null

    @Column(name = "uid")
    var uid: String? = null

    @Column(name = "unread_messages")
    var unreadMessages: Int = 0

    @Column(name = "last_message")
    var lastMessage: String? = null

    @Column(name = "last_timestamp")
    var lastTimestamp: String? = null

    constructor() {}

    constructor(cid: String?, uid: String?, unreadMessages: Int, lastMessage: String?, lastTimestamp: String?) {
        this.cid = cid
        this.uid = uid
        this.unreadMessages = unreadMessages
        this.lastMessage = lastMessage
        this.lastTimestamp = lastTimestamp
    }
}
