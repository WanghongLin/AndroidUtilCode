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
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

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

/**
 * Install a package from file path, need permission [android.Manifest.permission.REQUEST_INSTALL_PACKAGES]
 * since [Build.VERSION_CODES.O]
 *
 * @param filePath the APK file path to install
 * @param authorityOfFileProvider authority of file provider
 *
 * @return [Unit]
 */
fun Context.installApp(filePath: String, authorityOfFileProvider: String) {
    IntentUtils.forInstallApp(filePath, authorityOfFileProvider, this)
            .launchWithNewTaskCheck(this)
}

/**
 * Install a package from file path, and check the result code<br/>
 * Need permission since [android.Manifest.permission.REQUEST_INSTALL_PACKAGES]
 *
 * @param filePath the APK file path to install
 * @param authorityOfFileProvider authority of file provider, used since [Build.VERSION_CODES.N]
 * @param requestCode a request code with [Activity.startActivityForResult]
 *
 * @return [Unit]
 */
fun Activity.installApp(filePath: String, authorityOfFileProvider: String, requestCode: Int) {
    IntentUtils.forInstallApp(filePath, authorityOfFileProvider, this)
            .launchForResult(this, requestCode)
}

/**
 * Install a package by invoke the action [Intent.ACTION_INSTALL_PACKAGE]
 *
 * @param filePath the APK file path to install
 * @return [Unit]
 */
fun Context.installPackage(filePath: String) {
    IntentUtils.forInstallPackage(filePath)
            .launchWithNewTaskCheck(this)
}

/**
 * Install application with `pm` command
 *
 * @param filePath the full path of the apk file
 *
 * @return true if successfully install else false
 */
fun installAppSilently(filePath: String): Boolean {
    if (filePath.isFileExists()) {
        val result = ShellUtils.exec("pm install $filePath")
        return result.status == ShellUtils.EXEC_SUCCESS
    }
    return false
}

/**
 * Uninstall application
 * @param packageName the package name to uninstall
 * @param requestCode if not -1, a request code to check the result inside [Activity.onActivityResult]
 */
fun Context.uninstallApp(packageName: String, requestCode: Int = -1) {
    val intent = IntentUtils.forUninstallApp(packageName)
    if (requestCode != -1) {
        intent.launchWithNewTaskCheck(this)
    } else {
        intent.launchForResult(this, requestCode)
    }
}

/**
 * Uninstall application with <code>pm</code> command
 *
 * @param packageName the package name
 * @param keepData should keep the application data with `-k` option
 *
 * @return true if successfully else false
 */
fun uninstallAppSilently(packageName: String, keepData: Boolean = false): Boolean {
    val result = ShellUtils.exec("pm uninstall " +
            if (keepData) "-k " else " " + packageName)
    return result.status == ShellUtils.EXEC_SUCCESS
}

/**
 * Launch an application
 *
 * @param packageName the package name
 * @param requestCode request code for [Activity.startActivityForResult]
 */
fun Context.launchApp(packageName: String, requestCode: Int = -1) {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    if (requestCode != -1) {
        intent.launchForResult(this, -1)
    } else{
        intent.launchWithNewTaskCheck(this)
    }
}
