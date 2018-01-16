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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 * Created by mutter on 1/3/18.
 */

object AppUtils {

}

/**
 * Check if an application installed
 * @param action the action for the intent
 * @param category the category for the intent
 * @return true if the application existing
 */
fun Context.isAppInstalled(action: String, category: String): Boolean {
    val intent = Intent()
    intent.action = action
    intent.addCategory(category)
    return packageManager.resolveActivity(intent, 0) != null
}

/**
 * Check if an application installed
 * @param packageName the package name (e.g com.android.vending)
 * @return true if the application existing
 */
fun Context.isAppInstalled(packageName: String): Boolean {
    try {
        if (packageManager.getApplicationInfo(packageName, 0) != null) {
            return true
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return false
}
