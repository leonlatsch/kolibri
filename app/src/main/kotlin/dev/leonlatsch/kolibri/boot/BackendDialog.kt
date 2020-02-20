package dev.leonlatsch.kolibri.boot

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import dev.leonlatsch.kolibri.BuildConfig
import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.constants.Regex
import dev.leonlatsch.kolibri.constants.Responses
import dev.leonlatsch.kolibri.rest.dto.Container
import dev.leonlatsch.kolibri.rest.service.CommonService
import dev.leonlatsch.kolibri.rest.service.ConfigService
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory
import dev.leonlatsch.kolibri.settings.Config
import dev.leonlatsch.kolibri.util.empty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

/**
 * A [AlertDialog] to configure the backend
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class BackendDialog(context: Context) : AlertDialog(context) {

    private val connectButton: TextView
    private val hostnameEditText: EditText
    private val progressBar: ProgressBar
    private val successImageView: ImageView

    private var commonService: CommonService? = null
    private var configService: ConfigService? = null

    init {
        val view = layoutInflater.inflate(R.layout.dialog_backend, null)
        connectButton = view.findViewById(R.id.dialog_backend_button)
        hostnameEditText = view.findViewById(R.id.dialog_backend_edit_text)
        progressBar = view.findViewById(R.id.dialog_backend_progressbar)
        successImageView = view.findViewById(R.id.dialog_backend_success_indicator)

        connectButton.setOnClickListener { connect() }
        hostnameEditText.setOnEditorActionListener { _, _, _ ->
            connect()
            true
        }

        setCancelable(false)
        setView(view)
    }

    private fun connect() {
        isLoading(true)
        val url = buildUrl(hostnameEditText.text.toString())
        if (url == null) {
            isLoading(false)
            success(false)
            return
        }

        tryHealthcheck(url)
    }

    private fun tryHealthcheck(url: String) {
        RestServiceFactory.initialize(url)
        commonService = RestServiceFactory.getCommonService()
        configService = RestServiceFactory.getConfigService()
        commonService!!.healthcheck().enqueue(object : Callback<Container<Void>> {
            override fun onResponse(call: Call<Container<Void>>, response: Response<Container<Void>>) {
                if (response.isSuccessful && Responses.MSG_OK == response.body()?.message) {
                    saveConfig(url)
                } else {
                    isLoading(false)
                    success(false)
                }
            }

            override fun onFailure(call: Call<Container<Void>>, t: Throwable) {
                isLoading(false)
                success(false)
            }
        })
    }

    private fun checkVersion(backendVersion: String): Boolean {
        try {
            val majorVersion = BuildConfig.VERSION_NAME.split("\\.")[0] // Get the major version eg. 1 from 1.5.3
            return backendVersion.startsWith(majorVersion)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }

    }

    /**
     * Build a url with the default protocol
     *
     * @param input
     * @return
     */
    private fun buildUrl(input: String): String? {
        var input = input
        if (!Pattern.matches(Regex.URL, input)) {
            input = HTTPS + input
            if (!Pattern.matches(Regex.URL, input)) {
                return null
            }
        }

        if (!input.endsWith(SLASH)) {
            input += SLASH
        }

        return input
    }

    private fun success(success: Boolean) {
        successImageView.visibility = View.VISIBLE
        if (success) {
            successImageView.setImageDrawable(context.resources.getDrawable(R.drawable.icons8_checked_48, context.theme))
            connectButton.setText(R.string.CONNECTED)
            connectButton.setOnClickListener(null)
        } else {
            successImageView.setImageDrawable(context.resources.getDrawable(R.drawable.icons8_cancel_48, context.theme))
            connectButton.setText(R.string.CONNECT)
            connectButton.setOnClickListener { connect() }
        }
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            hostnameEditText.isEnabled = false
            connectButton.isClickable = false
            progressBar.visibility = View.VISIBLE
            successImageView.visibility = View.GONE
        } else {
            hostnameEditText.isEnabled = true
            connectButton.isClickable = true
            progressBar.visibility = View.GONE
        }
    }

    /**
     * Saves the config after a healtheck succeeded
     *
     * @param url
     */
    private fun saveConfig(url: String) {
        val preferences = Config.getSharedPreferences(context)
        val editor = preferences.edit()

        editor.putString(Config.KEY_BACKEND_HTTP_BASEURL, url)
        editor.putString(Config.KEY_BACKEND_BROKER_HOST, extractHostname(url))

        configService!!.brokerPort().enqueue(object : Callback<Int> { // Get the port for the broker
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    val port = response.body()
                    editor.putInt(Config.KEY_BACKEND_BROKER_PORT, port!!)
                    commonService!!.version().enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if (response.isSuccessful && checkVersion(response.body()!!)) {
                                editor.apply()
                                isLoading(false)
                                success(true)
                                Handler().postDelayed({ dismiss() }, 1000) // Wait 1 sec before dismissing
                            } else {
                                isLoading(false)
                                success(false)
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            isLoading(false)
                            success(false)
                        }
                    })
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                isLoading(false)
                success(false) // Very unlikely
            }
        })
    }

    /**
     * Extract the hostname for the broker
     *
     * @param url
     * @return
     */
    private fun extractHostname(url: String): String? {
        if (Pattern.matches(Regex.URL, url)) {
            var brokerHost: String? = null

            if (url.startsWith(HTTPS)) { // Remove http protocol
                brokerHost = url.replace(HTTPS, String.empty())
            } else if (url.startsWith(HTTP)) {
                brokerHost = url.replace(HTTP, String.empty())
            }

            if (brokerHost!!.endsWith(SLASH)) { // Remove trailing slashes
                brokerHost = brokerHost.replace(SLASH, String.empty())
            }

            return brokerHost
        } else {
            return null
        }
    }

    companion object {

        private val SLASH = "/"
        private val HTTPS = "https://"
        private val HTTP = "http://"
    }
}
