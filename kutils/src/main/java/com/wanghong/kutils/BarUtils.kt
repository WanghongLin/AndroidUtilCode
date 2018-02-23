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

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

/**
 * Created by mutter on 2/23/18.
 */
class BarUtils {
}

/**
 * Get status bar height
 */
fun Resources.getStatusBarHeight(): Int {
    return getDimensionPixelSize(getIdentifier("status_bar_height", "dimen", "android"))
}

/**
 * Toggle status bar visibility
 *
 * @param visibility the desired visibility
 */
fun Activity.toggleStatusBarVisibility(visibility: Boolean) {
    if (visibility) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

/**
 * Check if status bar visible
 */
fun Activity.isStatusBarVisible(): Boolean {
    return window.attributes.flags.and(WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0
}

/**
 * Set status bar in light mode
 */
fun Activity.setStatusBarLightMode(isLightMode: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.decorView?.let {
            val visibility = it.systemUiVisibility
            it.systemUiVisibility = if (isLightMode) {
                visibility.or(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            } else {
                visibility.and(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
            }
        }
    }
}

private const val TAG_KEY_OFFSET = -123

/**
 * Toggle the view top margin to status bar height
 *
 * @param shouldAddMargin should add margin to status bar height
 */
fun View.toggleMarginToEqualStatusHeight(shouldAddMargin: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        if (shouldAddMargin and getTag(TAG_KEY_OFFSET) as Boolean) return
        if (shouldAddMargin.not() and (getTag(TAG_KEY_OFFSET) as Boolean).not()) return
        with(layoutParams as ViewGroup.MarginLayoutParams) {
            setMargins(leftMargin,
                    if (shouldAddMargin) {
                        topMargin.plus(resources.getStatusBarHeight())
                    } else {
                        topMargin.minus(resources.getStatusBarHeight())
                    },
                    rightMargin,
                    bottomMargin)
            setTag(TAG_KEY_OFFSET, shouldAddMargin)
        }
    }
}

/**
 * Toggle notification bar visibility
 *
 * @param visibility the desired visibility
 */
@SuppressLint("WrongConstant", "PrivateApi")
fun Context.toggleNotificationBarVisibility(visibility: Boolean) {
    val statusBarService = getSystemService("statusbar")
    Class.forName("android.app.StatusBarManager").getMethod(
            if (visibility) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) "expand" else "expandNotificationsPanel"
            } else {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) "collapse" else "collapsePanels"
            }
    ).invoke(statusBarService)
}

/**
 * Get navigation bar height
 *
 * @return the navigation bar height
 */
fun Resources.getNavBarHeight(): Int {
    val id = getIdentifier("navigation_bar_height", "dimen", "android")
    if (id != 0) {
        return getDimensionPixelSize(id)
    }
    return 0
}

/**
 * Toggle navigation visibility
 *
 * @param visibility is visible
 */
fun Activity.toggleNavBarVisibility(visibility: Boolean) {
    if (visibility) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility.and(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv())
        }
    }
}

/**
 * Set navigation bar in immersive mode
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
fun Activity.setNavBarImmersive() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            .or(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
            .or(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
}

/**
 * Check navigation bar is visible
 *
 * @return true if visible else false
 */
fun Activity.isNavBarVisible(): Boolean {
    val isNoLimits = window.attributes.flags.and(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0
    if (isNoLimits) return false
    return window.decorView.systemUiVisibility.and(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0
}
