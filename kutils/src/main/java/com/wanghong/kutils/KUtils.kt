package com.wanghong.kutils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import kotlin.properties.Delegates

/**
 * Created by mutter on 1/3/18.
 */
object KUtils {

    var application: Application by Delegates.notNull()
    var topActivityWeakRef: WeakReference<Activity>? = null

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
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
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