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

    private val userInterface: UserInterface
    private val contactInterface: ContactInterface
    private val chatInterface: ChatInterface
    private val userService: UserService

    init {
        userInterface = UserInterface.getInstance()
        contactInterface = ContactInterface.getInstance()
        chatInterface = ChatInterface.getInstance()
        userService = RestServiceFactory.getUserService()
    }

    @Override
    fun execute(asyncJobCallback: AsyncJobCallback?) { //TODO: create backend function to get a list of contacts in one request
        run {
            val contacts = contactInterface.getALl()

            var success = true
            var contactsUpdated = 0

            for (contact in contacts) {
                try {
                    val contactResponse = userService.get(userInterface.getAccessToken(), contact.getUid()).execute()
                    val publicKeyResponse = userService.getPublicKey(userInterface.getAccessToken(), contact.getUid()).execute()

                    if (contactResponse.isSuccessful() && publicKeyResponse.isSuccessful()) {
                        val userDTO = contactResponse.body().getContent()
                        val publicKey = publicKeyResponse.body().getContent()

                        var changed = false

                        if (contact.getProfilePicTn() == null) {
                            if (userDTO.getProfilePicTn() != null) {
                                contact.setProfilePicTn(userDTO.getProfilePicTn())
                                changed = true
                            }
                        } else if (!contact.getProfilePicTn().equals(userDTO.getProfilePicTn())) {
                            contact.setProfilePicTn(userDTO.getProfilePicTn())
                            changed = true
                        }

                        if (!contact.getPublicKey().equals(publicKey)) {
                            contact.setPublicKey(publicKey)
                            changed = true
                        }

                        if (changed) {
                            contactInterface.updateContact(contact)
                            // Notify the chat list if it is already displayed
                            MessageConsumer.notifyChatListChangedFromExternal(chatInterface.getChatForContact(contact.getUid()))
                            contactsUpdated++
                        }
                    }
                } catch (e: IOException) {
                    success = false
                }

            }

            if (asyncJobCallback != null) {
                asyncJobCallback!!.onResult(JobResult(success, contactsUpdated))
            }
        }
    }
}
