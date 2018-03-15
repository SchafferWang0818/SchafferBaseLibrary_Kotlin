package com.schaffer.base.kotlin.common.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Process
import android.support.multidex.MultiDex
import com.schaffer.base.kotlin.common.manager.ActivityController
import com.schaffer.base.kotlin.common.manager.ActivityManager
import com.schaffer.base.kotlin.common.utils.Utils
import java.lang.ref.WeakReference

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date  : 2018/3/14
 * Project : SchafferBaseLibrary_Kotlin
 * Package : com.schaffer.base.kotlin.common.base
 * Description :
 */
class MyApplication : Application() {
    companion object {
        @get:Synchronized
        var instance: MyApplication? = null
            private set
    }

    var activityManager: ActivityManager? = null

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        activityManager = ActivityManager.screenManager
        registerActivityLifecycleCallbacks(DefinedActivityLifeCycleCallback())
        initLibrary()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
        MultiDex.install(base)
    }

    private fun init() {
        /* library */
    }

    class DefinedActivityLifeCycleCallback : Application.ActivityLifecycleCallbacks {


        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
            ActivityController.addActivity(activity)
//            BaseApplication.getRefWatcher(activity).watch(activity);
        }

        override fun onActivityStarted(activity: Activity) {
//            BlockLooper.getBlockLooper().start()
        }

        override fun onActivityResumed(activity: Activity) {
            ActivityController.setCurrActivity(WeakReference(activity))
        }

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {
//            BlockLooper.getBlockLooper().stop()
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            ActivityController.removeActivity(activity)
        }
    }

    private fun initLibrary() {
        Thread(Runnable {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            init()
        }).start()
    }
}
