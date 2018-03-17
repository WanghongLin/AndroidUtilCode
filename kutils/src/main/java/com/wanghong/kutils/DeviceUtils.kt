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
import android.content.Context
import android.net.wifi.WifiManager
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface

/**
 * Created by mutter on 3/17/18.
 */

@SuppressLint("HardwareIds")
class DeviceUtils {

    companion object {

        const val INVALID_MAC_ADDRESS = "02:00:00:00:00:00"

        val deviceAndroidID by lazy {
            Settings.Secure.getString(KUtils.application.contentResolver, Settings.Secure.ANDROID_ID)
        }

        val deviceMacAddress by lazy {
            fromWM() ?: fromIF() ?: fromIA() ?: fromConfig() ?: INVALID_MAC_ADDRESS
        }

        private val wifiInterfaceName by lazy {
            val result = ShellUtils.exec("getprop wifi.interface", true, false)
            if (result.successful() && result.stdout.isNotEmpty()) {
                result.stdout
            } else {
                "wlan0"
            }
        }

        fun isDeviceRooted(): Boolean {
            return listOf("/system/bin/", "/system/xbin/", "/sbin/",
                    "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/xbin/",
                    "/data/local/bin/", "/data/local/")
                    .any { File(it + File.separator + "su").exists() }
        }

        /**
         * MAC from wifi manager
         */
        @SuppressLint("MissingPermission")
        private fun fromWM(): String? {
            val wifiManager = KUtils.application.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (ContextCompat.checkSelfPermission(KUtils.application,
                            android.Manifest.permission.ACCESS_WIFI_STATE) == PermissionChecker.PERMISSION_GRANTED) {
                return wifiManager.connectionInfo.macAddress
            }
            return null
        }

        /**
         * MAC from interface
         */
        private fun fromIF(): String? {
            for (networkInterface in NetworkInterface.getNetworkInterfaces()) {
                if (wifiInterfaceName.equals(networkInterface.name, true)) {
                    return networkInterface.hardwareAddress.formattedAddress()
                }
            }
            return null
        }

        /**
         * MAC from [InetAddress]
         */
        private fun fromIA(): String? {
            val inetAddress = inetAddress()
            inetAddress?.let {
                return NetworkInterface.getByInetAddress(inetAddress).hardwareAddress.formattedAddress()
            }
            return null
        }

        /**
         * MAC from file exported by kernel
         */
        private fun fromConfig(): String? {
            // FIXME: does it really works?
            val result = ShellUtils.exec("cat /sys/class/net/$wifiInterfaceName/address", true)
            if (result.successful() && result.stdout.isNotEmpty()) {
                return result.stdout
            }
            return null
        }

        private fun inetAddress(): InetAddress? {
            for (networkInterface in NetworkInterface.getNetworkInterfaces()) {
                for (inetAddress in networkInterface.inetAddresses) {
                    if (inetAddress.isLoopbackAddress.not().and(inetAddress.hostAddress.indexOf(':') < 0)) {
                        return inetAddress
                    }
                }
            }
            return null
        }
    }
}

private fun ByteArray.formattedAddress(): String {
    val sb = StringBuilder()
    forEach { sb.append(String.format("%02x:", it)) }
    return sb.substring(0, sb.length-1);
}