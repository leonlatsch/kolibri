package dev.leonlatsch.kolibri.database.model

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
@Table(name = "message")
class Message : Model {

    @Column(name = "mid", index = true)
    var mid: String? = null

    @Column(name = "cid")
    var cid: String? = null

    @Column(name = "uid_from")
    var from: String? = null

    @Column(name = "uid_to")
    var to: String? = null

    @Column(name = "type")
    var type: MessageType? = null

    @Column(name = "timestamp")
    var timestamp: String? = null

    @Column(name = "content")
    var content: String? = null

    @Column(name = "sent")
    var isSent: Boolean = false

    constructor() {}

    constructor(mid: String?, cid: String?, from: String?, to: String?, type: MessageType?, timestamp: String?, content: String?) {
        this.mid = mid
        this.cid = cid
        this.from = from
        this.to = to
        this.type = type
        this.timestamp = timestamp
        this.content = content
        this.isSent = false
    }
}
