package dev.leonlatsch.kolibri.boot.jobs

import android.content.Context
import android.content.Intent
import android.os.Handler
import dev.leonlatsch.kolibri.boot.BootActivity
import dev.leonlatsch.kolibri.boot.jobs.base.AsyncJob
import dev.leonlatsch.kolibri.boot.jobs.base.AsyncJobCallback
import dev.leonlatsch.kolibri.boot.jobs.base.JobResult
import dev.leonlatsch.kolibri.constants.Responses
import dev.leonlatsch.kolibri.database.interfaces.ChatInterface
import dev.leonlatsch.kolibri.database.interfaces.ContactInterface
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.rest.dto.Container
import dev.leonlatsch.kolibri.rest.dto.UserDTO
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory
import dev.leonlatsch.kolibri.rest.service.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Async job to check and update the logged in user
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class CheckUserAsyncJob(context: Context) : AsyncJob(context) {

    private val userService: UserService
    private val userInterface: UserInterface
    private val contactInterface: ContactInterface
    private val chatInterface: ChatInterface

    init {
        userInterface = UserInterface.getInstance()
        userService = RestServiceFactory.getUserService()
        contactInterface = ContactInterface.getInstance()
        chatInterface = ChatInterface.getInstance()
    }

    @Override
    fun execute(asyncJobCallback: AsyncJobCallback) {
        run {
            userInterface.loadUser()

            val savedUser = userInterface.getUser()

            if (savedUser != null) {
                asyncJobCallback.onResult(JobResult<Void>(true, null))

                val call = userService.get(userInterface.getAccessToken())
                call.enqueue(object : Callback<Container<UserDTO>>() {
                    @Override
                    fun onResponse(call: Call<Container<UserDTO>>, response: Response<Container<UserDTO>>) {
                        if (response.code() === Responses.CODE_OK) { // Update saved user
                            userInterface.save(response.body().getContent(), savedUser!!.getAccessToken())
                        } else {
                            // if the saved user is not in the backend
                            userInterface.delete(savedUser)
                            contactInterface.deleteAll()
                            chatInterface.deleteAll()
                            Handler(getContext().getMainLooper()).post({
                                val intent = Intent(getContext(), BootActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                getContext().startActivity(intent)
                            })
                        }
                    }

                    @Override
                    fun onFailure(call: Call<Container<UserDTO>>, t: Throwable) {

                    }
                })
            } else {
                asyncJobCallback.onResult(JobResult<Void>(false, null))
            }
        }
    }
}
