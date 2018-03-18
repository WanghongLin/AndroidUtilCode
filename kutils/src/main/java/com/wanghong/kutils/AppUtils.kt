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
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import java.io.File
import java.security.MessageDigest

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

/**
 * Exit the calling app
 */
fun Context.exitApp() {
    KUtils.sActivityList.reversed().forEach { it ->
        it?.finish()
        KUtils.sActivityList.remove(it)
    }
    
    System.exit(0)
}

/**
 * Jump to app detail settings
 *
 * @param pkgName the package name, it's the current package if not specify
 */
fun Context.goToAppDetailSettings(pkgName: String = packageName) {
    IntentUtils.forAppDetailSettings(pkgName)
            .launchWithNewTaskCheck(this)
}

/**
 * Get application name
 */
fun Context.getApplicationName(pkgName: String = packageName): String {
    return packageManager.getApplicationInfo(pkgName, 0).loadLabel(packageManager).toString()
}

/**
 * Get application icon
 */
fun Context.getApplicationIcon(pkgName: String = packageName): Drawable {
    return packageManager.getApplicationIcon(pkgName)
}

/**
 * Get apk path of specified package in the system
 */
fun Context.getApplicationPath(pkgName: String = packageName): String {
    return packageManager.getApplicationInfo(pkgName, 0).sourceDir
}

/**
 * Get package version name
 */
fun Context.getApplicationVersionName(pkgName: String = packageName): String {
    return packageManager.getPackageInfo(pkgName, 0).versionName
}

val Context.applicationVersionName: String by lazy(LazyThreadSafetyMode.NONE) {
    KUtils.application.packageManager.getPackageInfo(KUtils.application.packageName, 0).versionName
}

/**
 * Get package version code
 */
fun Context.getApplicationVersionCode(pkgName: String = packageName): Int {
    return packageManager.getPackageInfo(pkgName, 0).versionCode
}

val Context.applicationVersionCode: Int by lazy(LazyThreadSafetyMode.NONE) {
    KUtils.application.packageManager.getPackageInfo(KUtils.application.packageName, 0).versionCode
}

/**
 * Check if a system application
 */
fun Context.isSystemApp(pkgName: String = packageName): Boolean {
    return packageManager.getApplicationInfo(pkgName, 0).flags.and(ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM
}

/**
 * Check if this is a debuggable application
 */
fun Context.isDebuggableApp(pkgName: String = packageName): Boolean {
    return packageManager.getApplicationInfo(pkgName, 0).flags.and(ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE
}

/**
 * Get application signature
 *
 * @param pkgName the package name
 * @param sha1sum should output as sha1sum
 *
 * @return [Array] of signature if sha1sum is false, else a sha1sum string split by colon
 */
@SuppressLint("PackageManagerGetSignatures")
fun Context.getApplicationSignature(pkgName: String = packageName, sha1sum: Boolean = false): Any {
    val signature = packageManager.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES).signatures
    return if (sha1sum) {
        MessageDigest.getInstance("SHA1")
                .hexChecksum(signature[0].toByteArray(), null)
                .replace(Regex("(?<=\\w{2}\\w{2})"), ":$0")
    } else {
        signature
    }
}

/**
 * Check if an app is in foreground
 *
 * @param pkgName the package name
 * @return true if it's in foreground else false
 */
fun Context.isForegroundApp(pkgName: String = packageName): Boolean {
    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return am.runningAppProcesses.find {
        it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }?.processName.equals(pkgName)
}

/**
 * Clear all app data, including files, cache, database, shared preferences and custom directory if provided
 *
 * @param customDirectories provide a list of directory to clear also
 */
fun Context.clearAppData(vararg customDirectories: File) {
    val appDataRoot = filesDir.absoluteFile.parent

    // file
    filesDir.emptyDirectory()

    // cache
    cacheDir.emptyDirectory()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        externalCacheDirs.forEach { it.emptyDirectory() }
    } else {
        externalCacheDir.emptyDirectory()
    }

    // database and shared preference
    File(appDataRoot + File.separator + SysConstants.DATABASE_DIR_NAME).emptyDirectory()
    File(appDataRoot + File.separator + SysConstants.SHARED_PREFS_DIR_NAME).emptyDirectory()

    // custom directory
    customDirectories.filter { it.isDirectory }.forEach { it.emptyDirectory() }
}
