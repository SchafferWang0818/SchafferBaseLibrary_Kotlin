package com.schaffer.base.kotlin.common.manager

import android.app.Activity
import java.lang.ref.WeakReference
import java.util.*

/**
 * 管理activity
 *
 * @author Schaffer
 */
class ActivityManager constructor() {

    // 退出栈顶Activity
    fun popActivity(activity: Activity?) {
        if (activity != null) {
            // 在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            activity.finish()
            activityStack!!.remove(WeakReference(activity))
        }
    }

    // 获得当前栈顶Activity
    fun currentActivity(): Activity? {
        var activity: Activity? = null
        if (!activityStack!!.empty()) {
            activity = activityStack!!.lastElement().get()
        }
        return activity
    }

    // 将当前Activity推入栈中
    fun pushActivity(activity: Activity) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack!!.add(WeakReference(activity))
    }

    // 退出栈中所有Activity
    fun popAllActivityExceptOne(cls: Class<*>) {
        while (true) {
            val activity = currentActivity() ?: break
            if (activity.javaClass == cls) {
                break
            }
            popActivity(activity)
        }
    }

    companion object {
        private var activityStack: Stack<WeakReference<Activity>>? = null
        private var instance: ActivityManager?=null
        var screenManager: ActivityManager ?= null
            get() {
                if (instance == null) {
                    instance = ActivityManager()
                }
                return instance
            }
    }
}
