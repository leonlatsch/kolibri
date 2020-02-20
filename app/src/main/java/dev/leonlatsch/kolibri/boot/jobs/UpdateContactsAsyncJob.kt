package dev.leonlatsch.kolibri.boot.jobs

import android.content.Context
import dev.leonlatsch.kolibri.boot.jobs.base.AsyncJob
import dev.leonlatsch.kolibri.boot.jobs.base.AsyncJobCallback
import dev.leonlatsch.kolibri.boot.jobs.base.JobResult
import dev.leonlatsch.kolibri.broker.MessageConsumer
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory
import dev.leonlatsch.kolibri.rest.service.UserService
import java.io.IOException

/**
 * Async job to update the saved contacts.
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class UpdateContactsAsyncJob(context: Context) : AsyncJob(context) {
    private val userService: UserService = RestServiceFactory.getUserService()

    override fun execute(asyncJobCallback: AsyncJobCallback) { //TODO: create backend function to get a list of contacts in one request
        run {
            val contacts = ContactInterface.all

            var success = true
            var contactsUpdated = 0

            for (contact in contacts) {
                try {
                    val contactResponse = userService.get(UserInterface.accessToken!!, contact.uid!!).execute()
                    val publicKeyResponse = userService.getPublicKey(UserInterface.accessToken!!, contact.uid!!).execute()

                    if (contactResponse.isSuccessful && publicKeyResponse.isSuccessful) {
                        val userDTO = contactResponse.body()?.content!!
                        val publicKey = publicKeyResponse.body()?.content!!

                        var changed = false

                        if (contact.profilePicTn == null) {
                            if (userDTO.profilePicTn != null) {
                                contact.profilePicTn = userDTO.profilePicTn
                                changed = true
                            }
                        } else if (!contact.profilePicTn.equals(userDTO.profilePicTn)) {
                            contact.profilePicTn = userDTO.profilePicTn
                            changed = true
                        }

                        if (!contact.publicKey.equals(publicKey)) {
                            contact.publicKey = publicKey
                            changed = true
                        }

                        if (changed) {
                            ContactInterface.updateContact(contact)
                            // Notify the chat list if it is already displayed
                            MessageConsumer.notifyChatListChangedFromExternal(ChatInterface.getChatForContact(contact.uid!!))
                            contactsUpdated++
                        }
                    }
                } catch (e: IOException) {
                    success = false
                }

            }

            asyncJobCallback.onResult(JobResult(success, contactsUpdated))
        }
    }
}
