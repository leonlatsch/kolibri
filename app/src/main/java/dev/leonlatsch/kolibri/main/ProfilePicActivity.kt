package dev.leonlatsch.kolibri.main

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.constants.Values
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.rest.dto.Container
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory
import dev.leonlatsch.kolibri.rest.service.UserService
import dev.leonlatsch.kolibri.util.AndroidUtils
import dev.leonlatsch.kolibri.util.ImageUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Activity to show the full profile picture of a user.
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class ProfilePicActivity : AppCompatActivity() {

    private var userInterface: UserInterface? = null

    private var userService: UserService? = null
    private var imageView: ImageView? = null
    private var progressOverlay: View? = null

    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pic)
        val toolbar = findViewById(R.id.profile_pic_toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        getSupportActionBar().setDisplayShowTitleEnabled(false)

        userInterface = UserInterface.getInstance()
        userService = RestServiceFactory.getUserService()

        val title = toolbar.findViewById(R.id.profile_pic_toolbar_text)
        imageView = findViewById(R.id.profile_pic_image_view)
        progressOverlay = findViewById(R.id.progressOverlay)

        title.setText(getIntent().getExtras().get(Values.INTENT_KEY_PROFILE_PIC_USERNAME) as String)
        val uid = getIntent().getExtras().get(Values.INTENT_KEY_PROFILE_PIC_UID) as String

        loadProfilePic(uid)
    }

    /**
     * Load and display a profile picture from the backend.
     *
     * @param uid The users uid
     */
    private fun loadProfilePic(uid: String) {
        isLoading(true)
        val context = this
        val call = userService!!.loadProfilePic(userInterface!!.getAccessToken(), uid)
        call.enqueue(object : Callback<Container<String>>() {
            @Override
            fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                if (response.isSuccessful()) {
                    val profilePic = response.body().getContent()
                    if (profilePic != null) {
                        imageView!!.setImageBitmap(ImageUtil.createBitmap(profilePic))
                    } else {
                        imageView!!.setImageDrawable(ImageUtil.getDefaultProfilePic(context))
                    }
                }
                isLoading(false)
            }

            @Override
            fun onFailure(call: Call<Container<String>>, t: Throwable) {
                isLoading(false)
                showDialog(getString(R.string.error), getString(R.string.error_no_internet))
                finish()
            }
        })
    }

    @Override
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f)
        } else {
            AndroidUtils.animateView(progressOverlay, View.GONE, 0.4f)
        }
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }
}
