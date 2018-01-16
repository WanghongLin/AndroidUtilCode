package com.wanghong.kutils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Created by mutter on 1/3/18.
 */
class ActivityUtils {
}

/**
 * Check activity existing, that means to check if it's declared in manifest
 *
 * @param packageName the package name, e.g com.example
 * @param className full path of class name, e.g com.example.MainActivity
 *
 * @return true if existing (declared in manifest) otherwise false
 */
fun Context.isActivityExists(packageName: String, className: String): Boolean {
    val intent = Intent()
    intent.setClassName(packageName, className)

    with(packageManager) {
        if (intent.resolveActivity(this) != null ||
                resolveActivity(intent, 0) != null ||
                queryIntentActivities(intent, 0) != null) {
            return true
        }

        return false
    }
}

fun Context.startActivity(kClass: Class<*>, extras: Bundle? = null, flags: Int = 0,
                          enterAnimation: Int = 0, exitAnimation: Int = 0) {
    val intent = Intent(this, kClass)
    if (extras != null) {
        intent.putExtras(extras)
    }
    intent.addFlags(flags)
    startActivityInternal(intent, enterAnimation, exitAnimation)
}

fun Context.startActivity(componentName: ComponentName, extras: Bundle? = null, flags: Int = 0,
                          enterAnimation: Int = 0, exitAnimation: Int = 0) {
    val intent = Intent()
    intent.component = componentName
    if (extras != null) {
        intent.putExtras(extras)
    }
    intent.addFlags(flags)
    startActivityInternal(intent, enterAnimation, exitAnimation)
}

fun Context.startHomeActivity() {
    val intent = Intent()
    intent.action = Intent.ACTION_MAIN
    intent.addCategory(Intent.CATEGORY_HOME)
    startActivityInternal(intent)
}

fun Context.getTopActivity() {

}

private fun Context.startActivityInternal(intent: Intent, enterAnimation: Int = 0, exitAnimation: Int = 0) {
    if (this is Activity) {
        overridePendingTransition(enterAnimation, exitAnimation)
    } else {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    intent.resolveActivity(packageManager)?.let {
        startActivity(intent)
    }
}
