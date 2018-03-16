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

import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by mutter on 3/14/18.
 */
class CrashUtils {

    companion object {

        val LOGGING_HEADER = "********** Crash log **********" +
                "\nAndroid Manufacturer   : " + Build.MANUFACTURER +
                "\nAndroid Model          : " + Build.MODEL +
                "\nAndroid Release Version: " + Build.VERSION.RELEASE +
                "\nAndroid SDK INT        : " + Build.VERSION.SDK_INT +
                "\nApp Version            : " + KUtils.application.applicationVersionName +
                "\nApp Version Code       : " + KUtils.application.applicationVersionCode +
                "\n********** Crash log **********\n";

        private lateinit var defaultHandler: Thread.UncaughtExceptionHandler

        /**
         * Set up an user provided exception handler
         *
         * @param dirName a directory to save crash file
         * @param listener extra handler from user, might be null
         */
        fun newUncaughtExceptionHandler(dirName: String = "crash", listener: ((Thread, Throwable) -> Unit)? = null) {
            defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

            Thread.setDefaultUncaughtExceptionHandler { t, e ->
                if (e != null) {
                    listener?.let { listener(t, e) }

                    thread {
                        e.writeToFile(uniqueCrashFile(dirName))
                    }
                }

                defaultHandler.uncaughtException(t, e)
            }
        }
    }
}

private fun uniqueCrashFile(dirName: String): File {
    return File(KUtils.application.filesDir.absolutePath + File.separator +
            dirName + File.separator +
            SimpleDateFormat("yyyy_MM_dd_HH_mm_ss.SSS", Locale.getDefault()).format(Date()));
}

private fun Throwable.writeToFile(file: File) {
    val printWriter = PrintWriter(OutputStreamWriter(FileOutputStream(file)))
    with(printWriter) {
        print(CrashUtils.LOGGING_HEADER)
        flush()
        printStackTrace(this)
        flush()
        close()
    }
}
