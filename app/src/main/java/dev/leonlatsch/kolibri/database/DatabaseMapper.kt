package dev.leonlatsch.kolibri.database

import dev.leonlatsch.kolibri.database.model.Contact
import dev.leonlatsch.kolibri.database.model.Message
import dev.leonlatsch.kolibri.database.model.User
import dev.leonlatsch.kolibri.rest.dto.MessageDTO
import dev.leonlatsch.kolibri.rest.dto.UserDTO

/**
 * Class for mapping dto to model and the other way
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
object DatabaseMapper {

    fun toModel(dto: UserDTO?): User? {
        if (dto == null) {
            return null
        }
        val user = User()
        user.uid = dto.uid
        user.email = dto.email
        user.username = dto.username
        user.profilePicTn = dto.profilePicTn
        return user
    }

    fun toDto(user: User?): UserDTO? {
        if (user == null) {
            return null
        }

        val dto = UserDTO()
        dto.uid = user.uid
        dto.username = user.username
        dto.email = user.email
        dto.password = user.password

        return dto
    }

    fun toModel(dto: MessageDTO?): Message? {
        if (dto == null) {
            return null
        }

        val message = Message()
        message.mid = dto.mid
        message.from = dto.from
        message.to = dto.to
        message.type = dto.type
        message.timestamp = dto.timestamp
        message.content = dto.content
        return message
    }

    fun toDto(message: Message?): MessageDTO? {
        if (message == null) {
            return null
        }

        val dto = MessageDTO()
        dto.mid = message.mid
        dto.from = message.from
        dto.to = message.to
        dto.type = message.type
        dto.timestamp = message.timestamp
        dto.content = message.timestamp
        return dto
    }

    fun toContact(userDTO: UserDTO?): Contact? {
        return toContact(toModel(userDTO))
    }

    fun toContact(user: User?): Contact? {
        if (user == null) {
            return null
        }

        val contact = Contact()
        contact.uid = user.uid
        contact.username = user.username
        contact.profilePicTn = user.profilePicTn
        return contact
    }

    fun toUser(contact: Contact?): User? {
        if (contact == null) {
            return null
        }

        val user = User()
        user.uid = contact.uid
        user.username = contact.username
        user.profilePicTn = contact.profilePicTn
        return user
    }
}
