package dev.leonlatsch.kolibri.main.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dev.leonlatsch.kolibri.R
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
import dev.leonlatsch.kolibri.util.AndroidUtils
import dev.leonlatsch.kolibri.util.empty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

/**
 * Activity for registration
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class RegisterActivity : AppCompatActivity() {

    private var progressOverlay: View? = null
    private var usernameEditText: EditText? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var passwordConfirmEditText: EditText? = null
    private var registerBtn: Button? = null

    private var userService: UserService? = null
    private var authService: AuthService? = null

    private var usernameValid: Boolean = false
    private var emailValid: Boolean = false
    private var passwordValid: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        progressOverlay = findViewById(R.id.progressOverlay)
        usernameEditText = findViewById(R.id.registerUsernameEditText)
        emailEditText = findViewById(R.id.registerEmailEditText)
        passwordEditText = findViewById(R.id.registerPasswordEditText)
        passwordConfirmEditText = findViewById(R.id.registerPasswordConfirmEditText)
        registerBtn = findViewById(R.id.registerNowBtn)

        userService = RestServiceFactory.getUserService()
        authService = RestServiceFactory.getAuthService()

        registerBtn!!.setOnClickListener { register() }

        usernameEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                validateUsername()
            }
        })

        emailEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                validateEmail()
            }
        })

        val passwordTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                validatePassword()
            }
        }

        passwordEditText!!.addTextChangedListener(passwordTextWatcher)
        passwordConfirmEditText!!.addTextChangedListener(passwordTextWatcher)

        loadPassedData()
        usernameEditText!!.requestFocus()
    }

    /**
     * Validate the value in the password EditText wit Regex and
     * compare with the confirm EditText.
     * Show a icon at the password edit text.
     */
    private fun validatePassword() {
        val password = passwordEditText!!.text.toString()
        val passwordConfirm = passwordConfirmEditText!!.text.toString()

        if (password.isEmpty() || !Pattern.matches(Regex.PASSWORD, password)) {
            showStatusIcon(passwordEditText!!, R.drawable.icons8_cancel_48)
            passwordValid = false
            return
        } else {
            showStatusIcon(passwordEditText!!, R.drawable.icons8_checked_48)
        }

        passwordValid = if (password == passwordConfirm) {
            showStatusIcon(passwordConfirmEditText!!, R.drawable.icons8_checked_48)
            true
        } else {
            showStatusIcon(passwordConfirmEditText!!, R.drawable.icons8_cancel_48)
            false
        }
    }

    /**
     * Validate the value in the email EditText with regex and backend.
     * Show an icon at the email EditText.
     */
    private fun validateEmail() {
        val email = emailEditText!!.text.toString()

        if (email.isEmpty() || !Pattern.matches(Regex.EMAIL, email)) {
            showStatusIcon(emailEditText!!, R.drawable.icons8_cancel_48)
            emailValid = false
            return
        }

        val call = userService!!.checkEmail(String.empty(), email)
        call.enqueue(object : Callback<Container<String>> {
            override fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message
                    emailValid = if (Responses.MSG_FREE == message) {
                        showStatusIcon(emailEditText!!, R.drawable.icons8_checked_48)
                        true
                    } else {
                        showStatusIcon(emailEditText!!, R.drawable.icons8_cancel_48)
                        false
                    }
                }
            }

            override fun onFailure(call: Call<Container<String>>, t: Throwable) {
                showDialog(getString(R.string.error), getString(R.string.error))
            }
        })
    }

    /**
     * Validate the values in the username EditText.
     * Show an icon at the username EditText.
     */
    private fun validateUsername() {
        val username = usernameEditText!!.text.toString()
        if (username.isEmpty() || username.length < 3) {
            showStatusIcon(usernameEditText!!, R.drawable.icons8_cancel_48)
            usernameValid = false
            return
        }

        val call = userService!!.checkUsername(String.empty(), username)
        call.enqueue(object : Callback<Container<String>> {
            override fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message
                    if (Responses.MSG_FREE == message) {
                        showStatusIcon(usernameEditText!!, R.drawable.icons8_checked_48)
                        usernameValid = true
                    } else {
                        usernameValid = false
                        showStatusIcon(usernameEditText!!, R.drawable.icons8_cancel_48)
                    }
                }
            }

            override fun onFailure(call: Call<Container<String>>, t: Throwable) {
                showDialog(getString(R.string.error), getString(R.string.error))
            }
        })
    }

    /**
     * Called when the register button is pressed.
     */
    private fun register() {
        isLoading(true)

        // Validate all input
        validateUsername()
        validateEmail()
        validatePassword()

        if (!usernameValid || !emailValid || !passwordValid) {
            isLoading(false)
            return
        }

        val userDTO = UserDTO()
        userDTO.email = emailEditText!!.text.toString()
        userDTO.username = usernameEditText!!.text.toString()
        userDTO.password = Hash.createHexHash(passwordEditText!!.text.toString())

        val keyPair = CryptoManager.genKeyPair()

        val call = authService!!.register(userDTO, keyPair?.publicKey!!)
        call.enqueue(object : Callback<Container<String>> {
            override fun onResponse(call: Call<Container<String>>, response: Response<Container<String>>) {
                isLoading(false)
                if (response.isSuccessful && Responses.MSG_OK == response.body()?.message) {
                    saveUserAndStartMain(response.body()?.content!!, keyPair)
                } else {
                    showDialog(getString(R.string.error), getString(R.string.error))
                }
            }

            override fun onFailure(call: Call<Container<String>>, t: Throwable) {
                isLoading(false)
                showDialog("Error", getString(R.string.error_no_internet))
            }
        })
    }

    /**
     * Save the new registered user after the registration has finished and
     * start the [MainActivity].
     *
     * @param accessToken The access token from the new user
     * @param keyPair     The new generated keypair
     */
    private fun saveUserAndStartMain(accessToken: String, keyPair: KeyPair) {
        val call = userService!!.get(accessToken)
        call.enqueue(object : Callback<Container<UserDTO>> {
            override fun onResponse(call: Call<Container<UserDTO>>, response: Response<Container<UserDTO>>) {
                if (response.isSuccessful) {
                    keyPair.uid = response.body()?.content!!.uid
                    KeyPairInterface.createOrGet(keyPair)
                    UserInterface.save(response.body()?.content!!, accessToken)
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
     * Show an icon at a EditText
     *
     * @param editText
     * @param drawable
     */
    private fun showStatusIcon(editText: EditText, drawable: Int) {
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
    }

    /**
     * Load the parsed email address from the [LoginActivity]
     */
    private fun loadPassedData() {
        if (intent.extras != null) {
            val passedUsername = intent.extras?.get(Values.INTENT_KEY_USERNAME) as String

            usernameEditText!!.setText(passedUsername)
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            AndroidUtils.animateView(progressOverlay!!, View.VISIBLE, 0.4f)
        } else {
            AndroidUtils.animateView(progressOverlay!!, View.GONE, 0.4f)
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
