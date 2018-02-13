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
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.content.FileProvider
import java.io.File

/**
 * Created by mutter on 1/19/18.
 */
class IntentUtils {

    companion object {
        private const val URI_TYPE_INSTALL_PACKAGE = "application/vnd.android.package-archive"

        /**
         * Create an intent for install application
         * @param filePath the full path of file
         * @param authorityOfFileProvider the authority of [FileProvider], used when API greater than [Build.VERSION_CODES.N]
         * @param context the application context, used by [FileProvider.getUriForFile] to get the uri from [FileProvider]
         *
         * @return an intent to install the package
         */
        fun forInstallApp(filePath: String, authorityOfFileProvider: String, context: Context? = null) : Intent {
            val intent = Intent(Intent.ACTION_VIEW)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context?.let {
                    intent.setDataAndType(
                            FileProvider.getUriForFile(context, authorityOfFileProvider, File(filePath)),
                            URI_TYPE_INSTALL_PACKAGE)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                intent.setDataAndType(Uri.fromFile(File(filePath)), URI_TYPE_INSTALL_PACKAGE)
            }
            return intent
        }

        fun forUninstallApp(packageName: String): Intent {
            return Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
            }
        }

        fun forInstallPackage(filePath: String) : Intent {
            return Intent(Intent.ACTION_INSTALL_PACKAGE).setData(Uri.fromFile(File(filePath)))
        }

        fun forAppDetailSettings(pkgName: String): Intent {
            return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", pkgName, null)
            }
        }
    }
}

/**
 * Launch this intent, check should add [Intent.FLAG_ACTIVITY_NEW_TASK] before launch
 */
val launchWithNewTaskCheck: Intent.(context: Context) -> Unit = {
    context ->
    if (context.packageManager.resolveActivity(this, 0) != null) {
        if (context !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(this)
    }
}

/**
 * Launch this intent with a request code and handle the result in [Activity.onActivityResult]
 */
val launchForResult: Intent.(context: Context, requestCode: Int) -> Unit = {
    context, requestCode ->
    if (context.packageManager.resolveActivity(this, 0) != null) {
        if (context is Activity) {
            context.startActivityForResult(this, requestCode)
        } else {
            throw RuntimeException(context.toString() + " must be activity")
        }
    }
}