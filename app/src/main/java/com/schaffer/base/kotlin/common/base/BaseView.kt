package com.schaffer.base.kotlin.common.base

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date  : 2018/3/14
 * Project : SchafferBaseLibrary_Kotlin
 * Package : com.schaffer.base.kotlin.common.base
 * Description :
 */
interface BaseView {
    fun showLoading(text: String)
    fun dismissLoading()
    fun onSucceed()
    fun onFailed(t: Throwable)
    fun onFailed()
    fun showToast(text: String)
    fun showSnackbar(text: String)
    fun showToast(resId: Int)
    fun showSnackbar(resId: Int)
    fun showLog(text: String)
}

