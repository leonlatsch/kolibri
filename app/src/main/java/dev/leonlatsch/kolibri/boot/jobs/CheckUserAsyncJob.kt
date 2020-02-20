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
import java.security.AccessController.getContext

/**
 * Async job to check and update the logged in user
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class CheckUserAsyncJob(context: Context) : AsyncJob(context) {

    private val userService: UserService = RestServiceFactory.getUserService()

    override fun execute(asyncJobCallback: AsyncJobCallback?) {
        runAsync(Runnable {
            UserInterface.loadUser()

            val savedUser = UserInterface.user

            if (savedUser != null) {
                asyncJobCallback?.onResult(JobResult(true, null))

                val call = userService.get(UserInterface.accessToken!!)
                call.enqueue(object : Callback<Container<UserDTO>> {
                    override fun onResponse(call: Call<Container<UserDTO>>, response: Response<Container<UserDTO>>) {
                        if (response.code() == Responses.CODE_OK) { // Update saved user
                            UserInterface.save(response.body()?.content!!, savedUser.accessToken!!)
                        } else {
                            // if the saved user is not in the backend
                            UserInterface.delete(savedUser)
                            ContactInterface.deleteAll()
                            ChatInterface.deleteAll()
                            Handler(context.mainLooper).post {
                                val intent = Intent(context, BootActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            }
                        }
                    }

                    override fun onFailure(call: Call<Container<UserDTO>>, t: Throwable) {

                    }
                })
            } else {
                asyncJobCallback?.onResult(JobResult(false, null))
            }
        })
    }
}
