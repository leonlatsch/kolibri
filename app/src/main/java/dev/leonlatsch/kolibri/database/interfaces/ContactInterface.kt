package dev.leonlatsch.kolibri.database.interfaces

import com.activeandroid.query.Delete
import com.activeandroid.query.Select
import com.activeandroid.query.Update
import dev.leonlatsch.kolibri.database.DatabaseMapper

import dev.leonlatsch.kolibri.database.model.Contact
import dev.leonlatsch.kolibri.database.model.User
import dev.leonlatsch.kolibri.rest.dto.UserDTO

/**
 * Database interface to persist contacts
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
object ContactInterface {

    private const val QUEUE_UID_WHERE = "uid = ?"

    val aLl: List<Contact>
        get() = Select().from(Contact::class.java).execute()

    fun getContact(uid: String): Contact {
        return Select().from(Contact::class.java).where(QUEUE_UID_WHERE, uid).executeSingle()
    }

    fun delete(contact: Contact) {
        contact.delete()
    }

    fun delete(uid: String) {
        Delete().from(Contact::class.java).where(QUEUE_UID_WHERE, uid).execute<Contact>()
    }

    fun deleteAll() {
        Delete().from(Contact::class.java).execute<Contact>()
    }

    fun save(userDTO: UserDTO, publicKey: String): String? {
        return save(DatabaseMapper.toModel(userDTO), publicKey)
    }

    fun save(user: User?, publicKey: String): String? {
        val contact = DatabaseMapper.toContact(user)
        contact?.publicKey = publicKey
        contact?.uid = user?.uid
        return save(contact)
    }

    fun save(contact: Contact?): String? {
        val saved = Select().from(Contact::class.java).where(QUEUE_UID_WHERE, contact?.uid).executeSingle<Contact>()

        if (saved != null) {
            saved!!.delete()
        }

        contact?.save()
        return contact?.uid
    }

    fun updateContact(contact: Contact) {
        if (contact.profilePicTn != null) {
            Update(Contact::class.java).set("uid = ?, username = ?, profile_pic_tn = ?, public_key = ?",
                    contact.uid, contact.username, contact.profilePicTn, contact.publicKey)
                    .where(QUEUE_UID_WHERE, contact.uid).execute()
        } else {
            Update(Contact::class.java).set("uid = ?, username = ?, public_key = ?",
                    contact.uid, contact.username, contact.publicKey)
                    .where(QUEUE_UID_WHERE, contact.uid).execute()
        }
    }
}
