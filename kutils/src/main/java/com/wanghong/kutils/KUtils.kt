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
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by mutter on 1/3/18.
 */
object KUtils {

    var application: Application by Delegates.notNull()
    var topActivityWeakRef: WeakReference<Activity>? = null
    val sActivityList = LinkedList<Activity?>()

    private val callbacks: Application.ActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity?) {
        }

        override fun onActivityResumed(activity: Activity?) {
            setTopActivityWeakRef(activity)
        }

        override fun onActivityStarted(activity: Activity?) {
            setTopActivityWeakRef(activity)
        }

        override fun onActivityDestroyed(activity: Activity?) {
            sActivityList.remove(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            sActivityList.add(activity)
            setTopActivityWeakRef(activity)
        }
    }

    fun initialize(application: Application) {
        this.application = application
        this.application.registerActivityLifecycleCallbacks(callbacks)
    }

    private fun setTopActivityWeakRef(activity: Activity?) {
        activity?.let {
            if (topActivityWeakRef == null || topActivityWeakRef?.get() != activity) {
                topActivityWeakRef = WeakReference<Activity>(activity)
            }
        }
    }
}