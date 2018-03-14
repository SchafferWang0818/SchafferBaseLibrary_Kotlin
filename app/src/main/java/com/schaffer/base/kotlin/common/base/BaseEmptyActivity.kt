package com.schaffer.base.kotlin.common.base

import android.support.v7.app.AppCompatActivity
import com.schaffer.base.kotlin.common.utils.LtUtils

/**
 * @author : SchafferWang at AndroidSchaffer
 * @date  : 2018/3/14
 * Project : SchafferBaseLibrary_Kotlin
 * Package : com.schaffer.base.kotlin.common.base
 * Description :
 */
class BaseEmptyActivity<V : BaseView, P : BasePresenter<V>>: BaseView, AppCompatActivity() {

    override fun showLoading(text: String) {
    }

    override fun dismissLoading() {
    }

    override fun onSucceed() {
    }

    override fun onFailed(t: Throwable) {
    }

    override fun onFailed() {
    }

    override fun showToast(text: String) {
    }

    override fun showSnackbar(text: String) {
    }

    override fun showToast(resId: Int) {
        showLog(getString(resId))
        LtUtils.showToastShort(this.applicationContext,getString(resId))
    }

    override fun showSnackbar(resId: Int) {
    }

    override fun showLog(text: String) {
    }
}