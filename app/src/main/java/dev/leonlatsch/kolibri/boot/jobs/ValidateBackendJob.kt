package dev.leonlatsch.kolibri.boot.jobs

import android.content.Context
import android.content.SharedPreferences

import dev.leonlatsch.kolibri.boot.jobs.base.Job
import dev.leonlatsch.kolibri.boot.jobs.base.JobResult
import dev.leonlatsch.kolibri.settings.Config

import dev.leonlatsch.kolibri.settings.Config.KEY_BACKEND_BROKER_HOST
import dev.leonlatsch.kolibri.settings.Config.KEY_BACKEND_BROKER_PORT
import dev.leonlatsch.kolibri.settings.Config.KEY_BACKEND_HTTP_BASEURL

/**
 * Sync job to validate the backend config
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class ValidateBackendJob(context: Context) : Job(context) {

    private val preferences: SharedPreferences

    init {
        preferences = Config.getSharedPreferences(context)
    }

    @Override
    fun execute(): JobResult<Void> {
        val baseUrl = preferences.getString(KEY_BACKEND_HTTP_BASEURL, null)
        val brokerHost = preferences.getString(KEY_BACKEND_BROKER_HOST, null)
        val brokerPort = preferences.getInt(KEY_BACKEND_BROKER_PORT, 0)

        return if (baseUrl == null || brokerHost == null || brokerPort == 0) {
            JobResult(false, null)
        } else {
            JobResult(true, null)
        }
    }
}
