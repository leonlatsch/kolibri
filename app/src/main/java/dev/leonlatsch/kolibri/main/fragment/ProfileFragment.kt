package dev.leonlatsch.kolibri.main.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.theartofdev.edmodo.cropper.CropImage

import java.util.regex.Pattern

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.constants.Regex
import dev.leonlatsch.kolibri.constants.Responses
import dev.leonlatsch.kolibri.constants.Values
import dev.leonlatsch.kolibri.database.DatabaseMapper
import dev.leonlatsch.kolibri.database.EntityChangedListener
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.database.model.User
import dev.leonlatsch.kolibri.main.MainActivity
import dev.leonlatsch.kolibri.main.ProfilePicActivity
import dev.leonlatsch.kolibri.rest.dto.Container
import dev.leonlatsch.kolibri.rest.dto.UserDTO
import dev.leonlatsch.kolibri.rest.service.AuthService
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory
import dev.leonlatsch.kolibri.rest.service.UserService
import dev.leonlatsch.kolibri.security.Hash
import dev.leonlatsch.kolibri.util.AndroidUtils
import dev.leonlatsch.kolibri.util.ImageUtil
import dev.leonlatsch.kolibri.util.empty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Fragment for displaying and changing the logged in user
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class ProfileFragment : Fragment(), EntityChangedListener<User> {

    private var isReloadMode = false
    private var profilePicChanged = false
    private var passwordChanged = false
    private var passwordCache: String? = null

    private var userService: UserService? = null
    private var authService: AuthService? = null

    private var parent: MainActivity? = null
    private var profilePicImageView: ImageView? = null
    private var usernameEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var statusMessage: TextView? = null

    private var emailValid: Boolean = false

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        parent = activity as MainActivity
        profilePicImageView = view.findViewById<CardView>(R.id.profile_profile_pic_card).findViewById(R.id.profile_profile_pic)
        usernameEditText = view.findViewById(R.id.profile_username_editText)
        emailEditText = view.findViewById(R.id.profile_email_editText)
        val passwordEditText = view.findViewById<EditText>(R.id.profile_password_editText)
        val changeProfilePicFab = view.findViewById<FloatingActionButton>(R.id.profile_profile_pic_change)
        val saveBtn = view.findViewById<Button>(R.id.profile_saveBtn)
        val deleteAccount = view.findViewById<Button>(R.id.profile_deleteBtn)
        statusMessage = view.findViewById(R.id.profile_status_message)

        changeProfilePicFab.setOnClickListener { changeProfilePic() }

        saveBtn.setOnClickListener { saveBtn() }

        deleteAccount.setOnClickListener { deleteAccount() }

        passwordEditText.setOnClickListener { changePassword() }

        profilePicImageView!!.setOnClickListener { showProfilePic() }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                dataChanged()
            }
        }

        emailEditText!!.addTextChangedListener(textWatcher)


        UserInterface.addEntityChangedListener(this)

        userService = RestServiceFactory.getUserService()
        authService = RestServiceFactory.getAuthService()

        mapUserToView(UserInterface.user!!)
        displayStatusMessage(String.empty())

        return view
    }

    private fun validate() {
        validateEmail()
    }

    /**
     * Validate the email address
     */
    private fun validateEmail() {
        val email = emailEditText!!.text.toString()
        if (email.isEmpty() || !Pattern.matches(Regex.EMAIL, email)) {
            showStatusIcon(emailEditText!!, R.drawable.icons8_cancel_48)
            emailValid = false
            isLoading(false)
            return
        }

        val usernameCall = userService!!.checkEmail(UserInterface.accessToken!!, email)
        usernameCall.enqueue(object : Callback<Container<String>> {
            override fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                if (response.isSuccessful) {
                    if (Responses.MSG_FREE == response.body()?.message || Responses.MSG_TAKEN_BY_YOU == response.body()?.message) {
                        emailValid = true
                        showStatusIcon(emailEditText!!, 0)
                        save()
                    } else {
                        showStatusIcon(emailEditText!!, R.drawable.icons8_cancel_48)
                        isLoading(false)
                    }
                }
            }

            override fun onFailure(call: Call<Container<String>>, t: Throwable) {
                parent!!.showDialog(getString(R.string.error), getString(R.string.error))
                isLoading(false)
            }
        })
    }

    private fun dataChanged() {
        if (!isReloadMode) {
            displayStatusMessage(getString(R.string.unsaved_data))
        }
    }

    /**
     * Called when the user clicks the profile pic.
     */
    private fun showProfilePic() {
        val intent = Intent(parent!!.applicationContext, ProfilePicActivity::class.java)
        intent.putExtra(Values.INTENT_KEY_PROFILE_PIC_UID, UserInterface.user?.uid)
        intent.putExtra(Values.INTENT_KEY_PROFILE_PIC_USERNAME, UserInterface.user?.username)
        startActivity(intent)
    }

    private fun displayStatusMessage(message: String) {
        statusMessage!!.text = message
    }

    /**
     * Called when the user clicks the password EditText.
     * Show a standalone dialog that handles the password changing.
     */
    private fun changePassword() {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        builder.setTitle(getString(R.string.password))

        val view = layoutInflater.inflate(R.layout.dialog_password, null)
        builder.setView(view)

        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
            // Just initialize this button
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val oldPasswordEditText = view.findViewById<EditText>(R.id.password_old_password_EditText)
            val newPasswordEditText = view.findViewById<EditText>(R.id.password_new_password_EditText)
            val confirmPasswordEditText = view.findViewById<EditText>(R.id.password_confirm_password_EditText)

            val userDTO = UserDTO()
            userDTO.email = UserInterface.user?.email
            userDTO.password = Hash.createHexHash(oldPasswordEditText.text.toString())
            val call = authService!!.login(userDTO)
            call.enqueue(object : Callback<Container<String>> {
                override fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                    if (response.isSuccessful) {
                        if (Responses.MSG_AUTHORIZED == response.body()?.message) {
                            showStatusIcon(oldPasswordEditText, R.drawable.icons8_checked_48)
                            val password = newPasswordEditText.text.toString()
                            val passwordConfirm = confirmPasswordEditText.text.toString()

                            if (password.isNotEmpty() && Pattern.matches(Regex.PASSWORD, password)) {
                                showStatusIcon(newPasswordEditText, R.drawable.icons8_checked_48)
                                if (password == passwordConfirm) {
                                    showStatusIcon(confirmPasswordEditText, R.drawable.icons8_checked_48)
                                    passwordCache = password
                                    saveBtn()
                                    passwordChanged = true
                                    dialog.dismiss()
                                } else {
                                    showStatusIcon(confirmPasswordEditText, R.drawable.icons8_cancel_48)
                                }
                            } else {
                                showStatusIcon(newPasswordEditText, R.drawable.icons8_cancel_48)
                            }
                        } else {
                            showStatusIcon(oldPasswordEditText, R.drawable.icons8_cancel_48)
                        }
                    } else {
                        showStatusIcon(oldPasswordEditText, R.drawable.icons8_cancel_48)
                    }
                }

                override fun onFailure(call: Call<Container<String>>, t: Throwable) {
                    parent!!.showDialog(getString(R.string.error), getString(R.string.error_no_internet))
                }
            })
        }
    }

    /**
     * Called when the user clicks the delete account button.
     * Deletes the logged in user in the backend and logs out.
     */
    private fun deleteAccount() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    isLoading(true)
                    val call = userService!!.delete(UserInterface.accessToken!!)
                    call.enqueue(object : Callback<Container<String>> {
                        override fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                            if (response.isSuccessful) {
                                if (Responses.MSG_OK == response.body()?.message) {
                                    parent!!.logout()
                                } else {
                                    parent!!.showDialog(getString(R.string.error), getString(R.string.error_common))
                                }
                            }
                            isLoading(false)
                        }

                        override fun onFailure(call: Call<Container<String>>, t: Throwable) {
                            parent!!.showDialog(getString(R.string.error), getString(R.string.error_no_internet))
                            isLoading(false)
                        }
                    })
                }
                DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
            }
        }

        val builder = AlertDialog.Builder(parent, R.style.AlertDialogCustom)
        builder.setMessage(getString(R.string.are_you_sure_delete)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show()
    }

    /**
     * Called when the save button is pressed.
     */
    private fun saveBtn() {
        isLoading(true)
        validate()
    }

    /**
     * Validate all changed data, save it in the backend and notify other components.
     */
    private fun save() {
        val user = mapViewToUser()
        val dto = DatabaseMapper.toDto(user)

        if (profilePicChanged) {
            dto?.profilePicTn = extractBase64()
        }

        // delete local password
        user.password = null
        passwordCache = null

        val call = userService!!.update(UserInterface.accessToken!!, dto!!)
        call.enqueue(object : Callback<Container<String>> {
            override fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                if (response.isSuccessful) {
                    if (Responses.MSG_OK == response.body()?.message!!) {
                        saveNewUserAndAccessToken(response.body()?.content)
                        displayToast(R.string.account_saved)
                        displayStatusMessage(String.empty())
                    }
                } else {
                    parent!!.showDialog(getString(R.string.error), getString(R.string.error_common))
                    dataChanged()
                }
                isLoading(false)
            }

            override fun onFailure(call: Call<Container<String>>, t: Throwable) {
                parent!!.showDialog(getString(R.string.error), getString(R.string.error_no_internet))
                dataChanged()
                isLoading(false)
            }
        })
    }

    /**
     * Save the new user and the new access token.
     *
     * @param newAccessToken
     */
    private fun saveNewUserAndAccessToken(newAccessToken: String?) {
        val call: Call<Container<UserDTO>>
        val accessToken: String
        if (newAccessToken == null) {
            call = userService!!.get(UserInterface.accessToken!!)
            accessToken = UserInterface.accessToken!!
        } else {
            accessToken = newAccessToken
            call = userService!!.get(newAccessToken)
        }

        call.enqueue(object : Callback<Container<UserDTO>> {
            override fun onResponse(call: Call<Container<UserDTO>>, response: Response<Container<UserDTO>>) {
                if (response.isSuccessful) {
                    UserInterface.save(response.body()?.content!!, accessToken)
                }
            }

            override fun onFailure(call: Call<Container<UserDTO>>, t: Throwable) {
                parent!!.showDialog(getString(R.string.error), getString(R.string.error_no_internet))
            }
        })
    }

    private fun displayToast(text: Int) {
        Toast.makeText(parent, text, Toast.LENGTH_LONG).show()
    }

    private fun extractBase64(): String? {
        val bitmapDrawable = profilePicImageView!!.getDrawable() as BitmapDrawable
        return ImageUtil.createBase64(bitmapDrawable.bitmap!!)
    }

    private fun changeProfilePic() {
        AndroidUtils.createImageCropper(getString(R.string.apply)).start(context!!, this)
    }

    /**
     * Display a [User] at the fragments view.
     *
     * @param user
     */
    private fun mapUserToView(user: User) {
        isReloadMode = true
        if (user.profilePicTn != null) {
            profilePicImageView!!.setImageBitmap(ImageUtil.createBitmap(user.profilePicTn))
        }
        usernameEditText!!.setText(user.username)
        emailEditText!!.setText(user.username)
        isReloadMode = false
    }

    /**
     * Extract the views values and create a [User] from it.
     *
     * @return
     */
    private fun mapViewToUser(): User {
        val savedUser = UserInterface.user
        savedUser?.email = emailEditText!!.text.toString()
        if (passwordChanged) {
            savedUser?.password = Hash.createHexHash(passwordCache!!)
        } else {
            savedUser?.password = null
        }

        return savedUser!!
    }

    /**
     * Called when the image cropper returns with a url.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (result != null) {
                val resultUri = result.uri
                profilePicImageView!!.setImageBitmap(BitmapFactory.decodeFile(resultUri.path))
                profilePicChanged = true
                saveBtn()
            }
        }
    }

    override fun entityChanged(newEntity: User?) {
        if (newEntity != null) {
            mapUserToView(newEntity)
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            AndroidUtils.animateView(parent!!.progressOverlay!!, View.VISIBLE, 0.4f)
        } else {
            AndroidUtils.animateView(parent!!.progressOverlay!!, View.GONE, 0.4f)
        }
    }

    private fun showStatusIcon(editText: EditText, drawable: Int) {
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
    }
}
