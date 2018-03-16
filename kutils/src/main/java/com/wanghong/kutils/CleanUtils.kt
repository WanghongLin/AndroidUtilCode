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
import android.os.Build
import android.os.Environment
import java.io.File

/**
 * Just a simple wrapper of [FileUtils] to clean some common used directories of app
 *
 * Created by mutter on 3/14/18.
 */

class CleanUtils {

}

fun Context.cleanInternalCache() {
    cacheDir.emptyDirectory()
}

fun Context.cleanInternalFiles() {
    filesDir.emptyDirectory()
}

fun Context.cleanInternalDatabases(databaseName: String? = null) {
    var filePath = filesDir.parentFile.absolutePath + File.separator + SysConstants.DATABASE_DIR_NAME;
    if (databaseName != null) {
        filePath = filePath + File.separator + databaseName;
    }
    val file = File(filePath)
    if (file.isDirectory) {
        file.emptyDirectory()
    } else {
        file.delete()
    }
}

fun Context.cleanInternalSp() {
    File(filesDir.parentFile.absolutePath + File.separator + SysConstants.SHARED_PREFS_DIR_NAME).emptyDirectory()
}

fun Context.cleanExternalCache() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            externalCacheDirs.forEach {  it.emptyDirectory() }
        }
    }
}
