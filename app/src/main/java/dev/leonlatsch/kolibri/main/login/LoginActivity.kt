package dev.leonlatsch.kolibri.main.login

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import java.util.regex.Pattern

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.boot.BootActivity
import dev.leonlatsch.kolibri.constants.Regex
import dev.leonlatsch.kolibri.constants.Responses
import dev.leonlatsch.kolibri.constants.Values
import dev.leonlatsch.kolibri.database.interfaces.KeyPairInterface
import dev.leonlatsch.kolibri.database.interfaces.UserInterface
import dev.leonlatsch.kolibri.database.model.KeyPair
import dev.leonlatsch.kolibri.main.MainActivity
import dev.leonlatsch.kolibri.rest.dto.Container
import dev.leonlatsch.kolibri.rest.dto.UserDTO
import dev.leonlatsch.kolibri.rest.service.AuthService
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory
import dev.leonlatsch.kolibri.rest.service.UserService
import dev.leonlatsch.kolibri.security.CryptoManager
import dev.leonlatsch.kolibri.security.Hash
import dev.leonlatsch.kolibri.settings.Config
import dev.leonlatsch.kolibri.util.AndroidUtils
import dev.leonlatsch.kolibri.util.empty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * This Activity is used for logging in and directing to the Registration
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class LoginActivity : AppCompatActivity() {

    private var usernameEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var registerBtn: Button? = null
    private var loginBtn: Button? = null
    private var errorText: TextView? = null
    private var progressOverlay: View? = null
    private var disconnectButton: ImageView? = null

    private var userService: UserService? = null
    private var authService: AuthService? = null

    /**
     * Validate all input
     *
     * @return true/false
     */
    private val isInputValid: Boolean
        get() {
            var isValid = true

            if (!isEmailValid(usernameEditText!!.getText().toString())) {
                isValid = false
            }

            if (passwordEditText!!.getText().toString().isEmpty()) {
                isValid = false
            }

            return isValid
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userService = RestServiceFactory.getUserService()
        authService = RestServiceFactory.getAuthService()

        usernameEditText = findViewById(R.id.loginUsernameEditText)
        passwordEditText = findViewById(R.id.loginPasswordEditText)
        registerBtn = findViewById(R.id.loginRegisterNowBtn)
        loginBtn = findViewById(R.id.loginBtn)
        progressOverlay = findViewById(R.id.progressOverlay)
        errorText = findViewById(R.id.loginErrorTextView)
        disconnectButton = findViewById(R.id.disconnectButton)

        registerBtn!!.setOnClickListener { v -> register() }
        disconnectButton!!.setOnClickListener { v -> disconnect() }

        loginBtn!!.setOnClickListener { v -> login() }
        passwordEditText!!.setOnEditorActionListener { _, _, _ ->
            login()
            true
        }
    }

    /**
     * Empty the backend config and start a new [BootActivity]
     */
    private fun disconnect() {
        val onClickListener = DialogInterface.OnClickListener{ _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                val editor = Config.getSharedPreferences(this).edit()
                editor.remove(Config.KEY_BACKEND_HTTP_BASEURL)
                editor.remove(Config.KEY_BACKEND_BROKER_HOST)
                editor.remove(Config.KEY_BACKEND_BROKER_PORT)
                editor.apply()
                startActivity(Intent(applicationContext, BootActivity::class.java))
                finish()
            }
        }

        val builder = android.app.AlertDialog.Builder(this, R.style.AlertDialogCustom)
        builder.setMessage(getString(R.string.are_you_sure_disconnect))
                .setPositiveButton(getString(R.string.yes), onClickListener)
                .setNegativeButton(getString(R.string.no), onClickListener)
                .show()
    }

    /**
     * Called when the login button is pressed
     */
    private fun login() {
        isLoading(true)
        if (!isInputValid) {
            isLoading(false)
            displayError(getString(R.string.login_fail))
            return
        }
        displayError(String.empty())
        val userAuthDTO = UserDTO()
        userAuthDTO.username = usernameEditText!!.text.toString()
        userAuthDTO.password = Hash.createHexHash(passwordEditText!!.text.toString())

        val call = authService!!.login(userAuthDTO)
        call.enqueue(object : Callback<Container<String>> {
            override fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                if (Responses.CODE_OK == response.code()) {
                    saveUserAndStartMain(response.body()?.content!!)
                } else {
                    displayError(getString(R.string.login_fail))
                    isLoading(false)
                }
            }

            override fun onFailure(call: Call<Container<String>>, t: Throwable) {
                isLoading(false)
                showDialog(getString(R.string.error), getString(R.string.error_no_internet))
            }
        })
    }

    /**
     * Save the new logged in user and start the [MainActivity]
     *
     * @param accessToken
     */
    private fun saveUserAndStartMain(accessToken: String) {
        val call = userService!!.get(accessToken)
        call.enqueue(object : Callback<Container<UserDTO>> {
            override fun onResponse(call: Call<Container<UserDTO>>, response: Response<Container<UserDTO>>) {
                if (response.isSuccessful) {
                    val newKeyPair = KeyPairInterface.createOrGet(CryptoManager.genKeyPair(), response.body()?.content!!.uid!!)
                    UserInterface.save(response.body()?.content!!, accessToken)
                    updatePublicKey(newKeyPair?.publicKey!!)
                    isLoading(false)
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<Container<UserDTO>>, t: Throwable) {
                showDialog(getString(R.string.error), getString(R.string.error_no_internet))
            }
        })
    }

    /**
     * Update the public key in the backend
     *
     * @param publicKey
     */
    private fun updatePublicKey(publicKey: String) {
        val call = userService!!.updatePublicKey(UserInterface.accessToken!!, publicKey)
        call.enqueue(object : Callback<Container<String>> {
            override fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                if (response.isSuccessful) {
                    if (Responses.MSG_OK != response.body()?.message) {
                        showDialog(getString(R.string.error), getString(R.string.error))
                    }
                }
            }

            override fun onFailure(call: Call<Container<String>>, t: Throwable) {
                showDialog(getString(R.string.error), getString(R.string.error_no_internet))
            }
        })
    }

    /**
     * Route to the registration
     */
    private fun register() {
        val intent = Intent(applicationContext, RegisterActivity::class.java)
        cacheToIntent(intent)
        startActivity(intent)
    }

    private fun displayError(message: String) {
        errorText!!.text = message
    }

    private fun isEmailValid(email: String): Boolean {
        return email.isNotEmpty() || !Pattern.matches(Regex.EMAIL, email)
    }

    private fun cacheToIntent(intent: Intent) {
        val username = usernameEditText!!.text.toString()

        if (username.isNotEmpty()) {
            intent.putExtra(Values.INTENT_KEY_USERNAME, username)
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            AndroidUtils.animateView(progressOverlay!!, View.VISIBLE, 0.4f)
        } else {
            AndroidUtils.animateView(progressOverlay!!, View.GONE, 0.4f)
        }
        usernameEditText!!.isEnabled = !loading
        passwordEditText!!.isEnabled = !loading
        loginBtn!!.isEnabled = !loading
        registerBtn!!.isEnabled = !loading
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }
}
