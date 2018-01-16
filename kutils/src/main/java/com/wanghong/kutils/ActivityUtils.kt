/*
 * Copyright (C) 2018 mutter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wanghong.kutils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
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

/**
 * Start an activity
 * @param kClass the activity class
 * @param extras bundle extras, used by [Intent.putExtras]
 * @param flags flags add to the intent with [Intent.addFlags]
 * @param enterAnimation the animation used with [Activity.overridePendingTransition]
 * @param exitAnimation the animation used with [Activity.overridePendingTransition]
 */
fun Context.startActivity(kClass: Class<*>, extras: Bundle? = null, flags: Int = 0,
                          enterAnimation: Int = 0, exitAnimation: Int = 0) {
    val intent = Intent(this, kClass)
    if (extras != null) {
        intent.putExtras(extras)
    }
    intent.addFlags(flags)
    startActivityInternal(intent, enterAnimation, exitAnimation)
}

/**
 * Start and activity
 *
 * @param componentName the component name
 * @param extras bundle extras, used by [Intent.putExtras]
 * @param flags flags to add to the intent by [Intent.addFlags]
 * @param enterAnimation the animation used with [Activity.overridePendingTransition], could be 0
 * @param exitAnimation the animation used with [Activity.overridePendingTransition], could be 0
 */
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

/**
 * start to the HOME
 */
fun Context.startHomeActivity() {
    val intent = Intent()
    intent.action = Intent.ACTION_MAIN
    intent.addCategory(Intent.CATEGORY_HOME)
    startActivityInternal(intent)
}

/**
 * Get top of activity of current app
 * @return the top of activity of the foreground stack
 */
fun Context.getTopActivity(): Activity? {
    if (KUtils.topActivityWeakRef != null) {
        val activity = KUtils.topActivityWeakRef?.get();
        if (activity != null) {
            return activity
        }
    }

    val size = KUtils.sActivityList.size
    if (size > 0) {
        return KUtils.sActivityList.get(size-1)
    }

    return null
}

/**
 * check the existing in stack
 * @return true if existing otherwise false
 */
fun isActivityExistsInStack(activity: Activity): Boolean {
    return KUtils.sActivityList.any { it != null && it == activity }
}

/**
 * check the existing in stack
 * @return true if existing otherwise false
 */
fun isActivityExistsInStack(clz: Class<*>): Boolean {
    return KUtils.sActivityList.any { it != null && it.javaClass == clz }
}

/**
 * check the existing in stack
 * @return true if existing otherwise false
 */
fun Activity.existsInStack(): Boolean {
    return KUtils.sActivityList.any { it != null && it == this }
}

/**
 * Finish activity with animation override
 */
fun Activity.finish(enterAnimation: Int = 0, exitAnimation: Int = 0) {
    overridePendingTransition(enterAnimation, exitAnimation)
    finish()
}

/**
 * Get the icon of an activity, the icon attribute in manifest
 * @param componentName the component name
 * @param clz the activity class
 * @return a drawable indicate the icon
 */
fun Context.getActivityIcon(componentName: ComponentName? = null, clz: Class<*>? = null): Drawable? {
    if (componentName != null) {
        return packageManager.getActivityIcon(componentName)
    }

    if (clz != null) {
        val intent = Intent(this, clz)
        if (packageManager.resolveActivity(intent, 0) != null) {
            return packageManager.getActivityIcon(intent)
        }
    }

    return null
}

/**
 * Get the logo of an activity, the logo attribute in manifest
 * @param componentName the component name
 * @param clz the class represent the activity
 * @return a drawable indicate the logo
 */
fun Context.getActivityLogo(componentName: ComponentName? = null, clz: Class<*>? = null): Drawable? {
    if (componentName != null) {
        return packageManager.getActivityLogo(componentName)
    }

    if (clz != null) {
        val intent = Intent(this, clz)
        if (packageManager.resolveActivity(intent, 0) != null) {
            return packageManager.getActivityLogo(intent)
        }
    }

    return null
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
