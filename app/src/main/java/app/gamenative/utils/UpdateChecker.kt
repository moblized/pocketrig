package app.gamenative.utils

import android.content.Context
import app.gamenative.BuildConfig
import app.gamenative.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Serializable
data class UpdateInfo(
    val updateAvailable: Boolean,
    val versionCode: Int,
    val versionName: String,
    val downloadUrl: String,
    val releaseNotes: String? = null
)

/**
 * PocketRig: checks the latest GitHub release of this app's repo (not GameNative's server).
 * Releases are tagged v1.0.N, where N is the build number and the APK's versionCode.
 */
object UpdateChecker {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun checkForUpdate(context: Context): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(Constants.Misc.UPDATE_CHECK_URL)
                .header("Accept", "application/vnd.github+json")
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Timber.w("Update check failed: HTTP ${response.code}")
                    return@withContext null
                }
                val release = JSONObject(response.body?.string() ?: return@withContext null)
                val tag = release.optString("tag_name")
                val versionCode = tag.substringAfterLast('.').toIntOrNull() ?: return@withContext null
                val assets = release.optJSONArray("assets") ?: return@withContext null
                var apkUrl: String? = null
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    if (asset.optString("name").endsWith(".apk")) {
                        apkUrl = asset.optString("browser_download_url")
                        break
                    }
                }
                if (apkUrl.isNullOrEmpty()) return@withContext null

                val notes = release.optString("body").substringBefore("\n---").trim().take(1500)
                val info = UpdateInfo(
                    updateAvailable = versionCode > BuildConfig.VERSION_CODE,
                    versionCode = versionCode,
                    versionName = tag.removePrefix("v"),
                    downloadUrl = apkUrl,
                    releaseNotes = notes.ifEmpty { null },
                )
                Timber.i("Update check: latest=$tag, installed=${BuildConfig.VERSION_CODE}")
                return@withContext info
            }
        } catch (e: Exception) {
            Timber.e(e, "Error checking for updates")
        }
        return@withContext null
    }
}
