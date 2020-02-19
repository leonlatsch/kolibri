package dev.leonlatsch.kolibri.database.interfaces

import com.activeandroid.query.Select
import dev.leonlatsch.kolibri.database.DatabaseMapper

import dev.leonlatsch.kolibri.database.model.User
import dev.leonlatsch.kolibri.rest.dto.UserDTO

/**
 * Child of [CacheInterface]
 * Syncs the database table 'user' and the Entity [User]
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
object UserInterface : CacheInterface<User>() {

    val user: User?
        get() {
            if (model == null) {
                loadUser()
            }
            return model
        }

    val accessToken: String?
        get() {
            if (model == null) {
                loadUser()
            }

            return model?.accessToken
        }

    init {
        model = user
    }

    fun loadUser() {
        val list = Select().from(User::class.java).execute<User>()
        if (list.size <= 1) {
            if (list.size == 1) {
                model = list.get(0)
            } else {
                model = null
            }
        } else {
            throw RuntimeException("more than one user in database") // Should never happen case
        }
    }

    fun save(userDto: UserDTO, accessToken: String) {
        val user = DatabaseMapper.toModel(userDto)
        user?.accessToken = accessToken
        save(user)
    }

    @Override
    fun save(user: User) {
        super.save(user)
        loadUser()
    }
}
