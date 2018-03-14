package com.schaffer.base.kotlin.common.base

import android.os.Build
import com.schaffer.base.kotlin.common.utils.AppUtils
import com.schaffer.base.kotlin.common.utils.EncryptUtils
import com.schaffer.base.kotlin.common.utils.LtUtils
import com.schaffer.base.kotlin.common.utils.StringUtils
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.net.SocketTimeoutException



/**
 * Created by SchafferWang on 2017/5/13.
 */

open class BasePresenter<V : BaseView> {


    protected var compositeSubscription: CompositeSubscription? = null
    private val tag: String
    protected var mView: V? = null

    init {
        compositeSubscription = CompositeSubscription()
        tag = javaClass.simpleName
    }

    fun attach(view: V) {
        if (compositeSubscription == null) {
            compositeSubscription = CompositeSubscription()
        }
        this.mView = view
    }

    fun detach() {
        if (compositeSubscription != null) {
            compositeSubscription!!.unsubscribe()
        }
        compositeSubscription = null
        mView = null
    }

    fun showLog(content: String) {
        LtUtils.w(tag, content)
    }

    fun showToast(content: String) {
        if (mView != null) {
            mView!!.showToast(content)
        }
    }

    protected fun loading() {
        if (mView != null) {
            mView!!.showLoading("加载中...")
        }
    }

    protected fun dismissLoad() {
        if (mView != null) {
            mView!!.dismissLoading()
        }
    }

    //    protected boolean onResponse(BaseBean bean) {
    //        if (mView != null) mView.dismissLoading();
    //        if (bean.getErrcode() != 0) {
    //            if (mView != null) {
    //                if (bean.getErrstr().contains("必要参数")) {
    //                    mView.showLog(bean.getErrstr());
    //                } else {
    //                    mView.showToast(bean.getErrstr());
    //                }
    //            }
    //        }
    //
    //        return bean.getErrcode() == 0;
    //    }
    fun <T : OkHttpRequestBuilder<*>> addHeader(builder: T): T {
        val s = StringUtils.generateShortUuid()
        val t = System.currentTimeMillis() / 1000
        val sign_str = t.toString() + "Constants.WITHDRAW_INFO" + s
        val sign = EncryptUtils.encryptMD5ToString(sign_str.toUpperCase())
        return builder.addHeader("v",AppUtils.getVersionName(MyApplication.instance))
                .addHeader("sv", Build.MANUFACTURER + " " + Build.VERSION.SDK)
                .addHeader("sign", sign)
                .addHeader("t", t.toString())
                .addHeader("s", s) as T
    }

    protected fun onFailed(t: Throwable?) {
        try {
            if (t == null) {
                return
            }
            if (mView != null) {
                mView!!.dismissLoading()
                mView!!.showLog(t.message + "-->\n\t\t" + t.localizedMessage)
            }
            t.printStackTrace()
            if (t is SocketTimeoutException && mView != null) {
                mView!!.showToast("网络状况好像不太好")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mView!!.showToast("网络状况好像不太好")
        }
    }


    inner class SchedulerTransformer<T> : Observable.Transformer<T, T> {

        override fun call(observable: Observable<T>): Observable<T> {
            return observable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}
