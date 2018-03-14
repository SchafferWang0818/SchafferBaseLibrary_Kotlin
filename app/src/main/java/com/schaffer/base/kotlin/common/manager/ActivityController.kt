package com.schaffer.base.kotlin.common.manager

import android.app.Activity
import java.lang.ref.WeakReference
import java.util.*

/**
 * className: ActivityController
 * description: 活动管理类
 * datetime: 2016/4/5 0005 上午 8:54
 */
object ActivityController {

    private val activities = ArrayList<WeakReference<Activity>>()
    private var currActivity: WeakReference<Activity>? = null

    /**
     * 添加活动
     *
     * @param activity
     */
    fun addActivity(activity: Activity) {
        activities.add(WeakReference(activity))
    }

    /**
     * 销毁活动
     *
     * @param activity
     */
    fun removeActivity(activity: Activity) {
        activities.remove(WeakReference(activity))
    }

    /**
     * 销毁活动
     *
     * @param tag 活动标志
     */
    fun removeActivity(tag: String) {
        for (activity in activities) {
            val get = activity.get()
            if (get != null && !get.isFinishing) {
                if (get.javaClass.name == tag) {
                    get.finish()
                }
            }
        }
    }


    /**
     * 销毁所有活动
     */
    fun finishAll() {
        for (activity in activities) {
            val get = activity.get()
            if (get != null && !get.isFinishing()) {
                get.finish()
            }
        }
    }


    /**
     * 销毁指定活动之外的所有活动
     *
     * @param tags
     */
    @JvmStatic
    fun finishIgnoreTag(tags: Array<String>) {
        for (activity in activities) {
            val a = activity.get()
            if (a != null && !a.isFinishing) {
                var flag = true
                for (i in tags.indices) {
                    if (a.javaClass.name == tags[i]) {
                        flag = false
                    }
                }
                if (flag) {
                    a.finish()
                }
            }
        }
    }

    /**
     * 判断活动是否在集合里
     *
     * @param tag
     * @return
     */
    fun hasAdded(tag: String): Boolean {
        for (activity in activities) {
            val get = activity.get()
            if (get != null && get.javaClass.name == tag) {
                return true
            }
        }
        return false
    }


    /**
     * 设置当前Activity
     *
     * @param activity
     */
    fun setCurrActivity(activity: WeakReference<Activity>) {
        currActivity = activity
    }


    /**
     * 获取当前Activity
     *
     * @return
     */
    fun getCurrActivity(): Activity {
        return currActivity!!.get()!!
    }


}
