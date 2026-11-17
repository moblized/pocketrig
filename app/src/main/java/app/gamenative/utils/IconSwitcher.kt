package app.gamenative.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import app.gamenative.enums.AppTheme
import timber.log.Timber

/**
 * PocketRig: the launcher icon follows the app's theme.
 *
 * - System Default -> MainActivityAliasDefault, whose icon has a night variant, so it is light by day and
 *   dark when the phone is in dark mode.
 * - Light          -> MainActivityAliasLight (always the light icon).
 * - Dark / AMOLED  -> MainActivityAliasAlt (always the dark icon).
 *
 * Aliases are only touched when the wanted one isn't already the enabled one, because some launchers
 * redraw or move the home-screen icon each time an alias changes.
 */
object IconSwitcher {

    private const val ALIAS_DEFAULT = "app.gamenative.MainActivityAliasDefault"
    private const val ALIAS_LIGHT = "app.gamenative.MainActivityAliasLight"
    private const val ALIAS_DARK = "app.gamenative.MainActivityAliasAlt"
    private val ALL_ALIASES = listOf(ALIAS_DEFAULT, ALIAS_LIGHT, ALIAS_DARK)

    fun applyForTheme(context: Context, theme: AppTheme) {
        val wanted = when (theme) {
            AppTheme.AUTO -> ALIAS_DEFAULT
            AppTheme.DAY -> ALIAS_LIGHT
            AppTheme.NIGHT, AppTheme.AMOLED -> ALIAS_DARK
        }
        val packageManager = context.packageManager
        try {
            if (isEnabled(context, wanted) && ALL_ALIASES.none { it != wanted && isEnabled(context, it) }) return
            // Enable the new alias first so the app always has a launcher entry.
            packageManager.setComponentEnabledSetting(
                ComponentName(context, wanted),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP,
            )
            ALL_ALIASES.filter { it != wanted }.forEach {
                packageManager.setComponentEnabledSetting(
                    ComponentName(context, it),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP,
                )
            }
            Timber.i("Launcher icon switched to $wanted for theme $theme")
        } catch (e: Exception) {
            Timber.w(e, "Could not switch launcher icon")
        }
    }

    private fun isEnabled(context: Context, alias: String): Boolean {
        val state = context.packageManager.getComponentEnabledSetting(ComponentName(context, alias))
        return when (state) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> alias == ALIAS_DEFAULT // manifest default
            else -> false
        }
    }

    /** Kept for the old icon picker's call sites. */
    fun applyLauncherIcon(context: Context, useAltIcon: Boolean) =
        applyForTheme(context, if (useAltIcon) AppTheme.NIGHT else AppTheme.AUTO)
}
