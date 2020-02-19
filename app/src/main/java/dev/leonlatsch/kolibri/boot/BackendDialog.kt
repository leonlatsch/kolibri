package dev.leonlatsch.kolibri.boot

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Handler
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import java.util.regex.Pattern

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.constants.Regex
import dev.leonlatsch.kolibri.constants.Responses
import dev.leonlatsch.kolibri.constants.Values
import dev.leonlatsch.kolibri.rest.dto.Container
import dev.leonlatsch.kolibri.rest.service.CommonService
import dev.leonlatsch.kolibri.rest.service.ConfigService
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory
import dev.leonlatsch.kolibri.settings.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val view = getLayoutInflater().inflate(R.layout.dialog_backend, null)
        connectButton = view.findViewById(R.id.dialog_backend_button)
        hostnameEditText = view.findViewById(R.id.dialog_backend_edit_text)
        progressBar = view.findViewById(R.id.dialog_backend_progressbar)
        successImageView = view.findViewById(R.id.dialog_backend_success_indicator)

        connectButton.setOnClickListener({ view1 -> connect() })
        hostnameEditText.setOnEditorActionListener({ textView, i, keyEvent ->
            connect()
            true
        })

        setCancelable(false)
        setView(view)
    }

    private fun connect() {
        isLoading(true)
        val url = buildUrl(hostnameEditText.getText().toString())
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
        commonService!!.healthcheck().enqueue(object : Callback<Container<Void>>() {
            @Override
            fun onResponse(call: Call<Container<Void>>, response: Response<Container<Void>>) {
                if (response.isSuccessful() && Responses.MSG_OK.equals(response.body().getMessage())) {
                    saveConfig(url)
                } else {
                    isLoading(false)
                    success(false)
                }
            }

            @Override
            fun onFailure(call: Call<Container<Void>>, t: Throwable) {
                isLoading(false)
                success(false)
            }
        })
    }

    private fun checkVersion(backendVersion: String): Boolean {
        try {
            val packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0)
            val arr = packageInfo.versionName.split("\\.")
            val majorVersion = packageInfo.versionName.split("\\.")[0] // Get the major version eg. 1 from 1.5.3
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
        successImageView.setVisibility(View.VISIBLE)
        if (success) {
            successImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icons8_checked_48, getContext().getTheme()))
            connectButton.setText(R.string.CONNECTED)
            connectButton.setOnClickListener(null)
        } else {
            successImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icons8_cancel_48, getContext().getTheme()))
            connectButton.setText(R.string.CONNECT)
            connectButton.setOnClickListener({ view -> connect() })
        }
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            hostnameEditText.setEnabled(false)
            connectButton.setClickable(false)
            progressBar.setVisibility(View.VISIBLE)
            successImageView.setVisibility(View.GONE)
        } else {
            hostnameEditText.setEnabled(true)
            connectButton.setClickable(true)
            progressBar.setVisibility(View.GONE)
        }
    }

    /**
     * Saves the config after a healtheck succeeded
     *
     * @param url
     */
    private fun saveConfig(url: String) {
        val preferences = Config.getSharedPreferences(getContext())
        val editor = preferences.edit()

        editor.putString(Config.KEY_BACKEND_HTTP_BASEURL, url)
        editor.putString(Config.KEY_BACKEND_BROKER_HOST, extractHostname(url))

        configService!!.getBrokerPort().enqueue(object : Callback<Integer>() { // Get the port for the broker
            @Override
            fun onResponse(call: Call<Integer>, response: Response<Integer>) {
                if (response.isSuccessful()) {
                    val port = response.body()
                    editor.putInt(Config.KEY_BACKEND_BROKER_PORT, port)
                    commonService!!.getVersion().enqueue(object : Callback<String>() {
                        @Override
                        fun onResponse(call: Call<String>, response: Response<String>) {
                            if (response.isSuccessful() && checkVersion(response.body())) {
                                editor.apply()
                                isLoading(false)
                                success(true)
                                Handler().postDelayed({ dismiss() }, 1000) // Wait 1 sec before dismissing
                            } else {
                                isLoading(false)
                                success(false)
                            }
                        }

                        @Override
                        fun onFailure(call: Call<String>, t: Throwable) {
                            isLoading(false)
                            success(false)
                        }
                    })
                }
            }

            @Override
            fun onFailure(call: Call<Integer>, t: Throwable) {
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
                brokerHost = url.replace(HTTPS, Values.EMPTY)
            } else if (url.startsWith(HTTP)) {
                brokerHost = url.replace(HTTP, Values.EMPTY)
            }

            if (brokerHost!!.endsWith(SLASH)) { // Remove trailing slashes
                brokerHost = brokerHost.replace(SLASH, Values.EMPTY)
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
