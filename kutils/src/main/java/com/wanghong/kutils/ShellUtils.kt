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

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


/**
 * Created by mutter on 1/20/18.
 */

class ShellUtils {

    companion object {
        const val EXEC_SUCCESS = 0

        private val SHELL_ENV_CACHE = mutableMapOf<String, String>()

        private val LINE_SEPARATOR = System.getProperty("line.separator")

        private fun InputStream.toReadableString() : String {
            val reader = BufferedReader(InputStreamReader(this))
            val string = StringBuilder()
            reader.readLines().forEach {
                it ->
                string.append(it)
                string.append(LINE_SEPARATOR)
            }
            return string.toString()
        }

        /**
         * execute shell command
         *
         * @param cmd the command to execute
         * @param needStdout collect the stand out to the returning result
         * @param needStderr collect the stand err to the returning result
         *
         * @return [ShellExecResult] contain a status code and stdout/stderr
         */
        fun exec(cmd: String, needStdout: Boolean = false, needStderr: Boolean = false): ShellExecResult {
            val process = Runtime.getRuntime().exec("/system/bin/sh" + LINE_SEPARATOR)
            with(process.outputStream) {
                write(cmd.toByteArray())
                write(LINE_SEPARATOR.toByteArray())
                flush()
                close()
            }

            return ShellExecResult(process.waitFor(),
                    if (needStdout) process.inputStream.toReadableString() else "",
                    if (needStderr) process.errorStream.toReadableString() else "")
        }

        /**
         * Get shell variable value in shell environment
         *
         * @param envName environment name, e.g USER
         * @return the value
         */
        fun shellEnvironment(envName: String): String? {
            if (SHELL_ENV_CACHE.isEmpty()) {
                val result = exec("set", true, true)
                result.stdout.split("\n").filter { it ->
                    it.isNotEmpty()
                }.forEach {
                    val firstEqual = it.indexOfFirst { it == '=' }
                    if (firstEqual != -1) {
                        val name = it.substring(0, firstEqual)
                        val value = it.substring(firstEqual + 1)
                        SHELL_ENV_CACHE[name] = value
                    }
                }
            }

            return SHELL_ENV_CACHE[envName]
        }
    }
}

data class ShellExecResult(val status: Int, val stdout: String = "", val stderr: String = "")