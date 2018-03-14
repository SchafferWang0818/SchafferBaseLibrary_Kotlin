package com.schaffer.base.kotlin.common.base

import com.schaffer.base.kotlin.BuildConfig
import com.schaffer.base.kotlin.api.ApiInterface
import com.schaffer.base.kotlin.common.utils.LtUtils
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

/**
 * @author SchafferWang
 * @date 2017/5/13
 */

abstract class BaseModel<T> {


    val service: T

    /**
     * 获取接口定义参数等信息对应的接口
     *
     * @return
     */
    protected abstract val serviceClass: Class<T>

    init {
        if (retrofit == null) {
            synchronized(BaseModel::class.java) {
                if (retrofit == null) {
                    val client = OkHttpClient().newBuilder()
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .addInterceptor(createHttpLoggingInterceptor())
                            //.addInterceptor(new MultiBaseUrlInterceptor())//多BaseUrl的情况
                            .addInterceptor(MyHeaderInterceptor())
                            .build()

                    if (ApiInterface.HOST_BASE_URL.startsWith("https")) {
                        //https 跳过SSL认证
                        solveHttps(client)
                    }

                    retrofit = Retrofit.Builder()
                            .baseUrl(ApiInterface.HOST_BASE_URL)
                            .client(client)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            /*用于文本上传*/
                            //.addConverterFactory(new FileRequestBodyConverterFactory())
                            .build()
                }
            }
        }
        service = retrofit!!.create(serviceClass)

    }

    fun solveHttps(sClient: OkHttpClient): OkHttpClient {
        var sc: SSLContext? = null
        try {
            sc = SSLContext.getInstance("SSL")
            sc!!.init(null, arrayOf(TrustManager()), SecureRandom())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val hv1 = HostnameVerifier { hostname, session -> true }

        val workerClassName = "okhttp3.OkHttpClient"
        try {
            val workerClass = Class.forName(workerClassName)
            val hostnameVerifier = workerClass.getDeclaredField("hostnameVerifier")
            hostnameVerifier.isAccessible = true
            hostnameVerifier.set(sClient, hv1)

            val sslSocketFactory = workerClass.getDeclaredField("sslSocketFactory")
            sslSocketFactory.isAccessible = true
            sslSocketFactory.set(sClient, sc!!.socketFactory)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return sClient
    }

    private class TrustManager : X509TrustManager {

        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {

        }

        override fun getAcceptedIssuers(): Array<X509Certificate>? {
            /*return new X509Certificate[0]*/
            return null
        }
    }

    private class MyLogger : HttpLoggingInterceptor.Logger {

        override fun log(message: String) {
            if (BuildConfig.DEBUG) {
                if (message.startsWith("{") && message.endsWith("}")) {
                    LtUtils.d(">>>:\n" + message + "\n")
                } else {
                    LtUtils.d(message)
                }
            }
        }
    }

    private class MyHeaderInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val request = original.newBuilder()
                    ////版本号
                    //.header("v", "AppUtils.getVersionCode(BaseApplication.getInstance())")
                    ////系统版本号
                    //.header("sv", Build.MANUFACTURER + " " + Build.VERSION.SDK)
                    ////2=android
                    .header("type", "2")
                    .method(original.method(), original.body())
                    .build()
            return chain.proceed(request)
        }
    }


    internal class FileRequestBodyConverterFactory : Converter.Factory() {
        override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<Annotation>?, methodAnnotations: Array<Annotation>?, retrofit: Retrofit?): Converter<File, RequestBody> {
            return FileRequestBodyConverter()
        }
    }

    internal class FileRequestBodyConverter : Converter<File, RequestBody> {

        @Throws(IOException::class)
        override fun convert(file: File): RequestBody {
            return RequestBody.create(MediaType.parse("application/otcet-stream"), file)
        }
    }

    /**
     * 当base_url需要发生变化时需要加@Headers({"apiSign:shop"}) 作为一个标记
     * 例如 目标链接为 https://shop.fentuapp.com.cn/
     */
    private class MultiBaseUrlInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val oldHttpUrl = originalRequest.url()
            try {
                val builder = originalRequest.newBuilder()
                val headers = originalRequest.headers("apiSign")
                if (headers != null && headers.size > 0) {
                    builder.removeHeader("apiSign")
                    val s = headers[0]

                    var baseUrl: HttpUrl?
                    if (s == "shop") {
                        baseUrl = HttpUrl.parse(ApiInterface.HOST_BASE_URL)
                        val port = oldHttpUrl.newBuilder()
                                .scheme(baseUrl!!.scheme())
                                .host(baseUrl.host())
                                .port(baseUrl.port())
                        var newUrl: HttpUrl? = if (ApiInterface.HOST_BASE_URL.contains("fentu_server/public/")) {
                            //测试库
                            //http://192.168.1.167/fentu_server/public/
                            //https://shop.fentuapp.com.cn/
                            port.removePathSegment(0).removePathSegment(0).build()
                        } else {
                            //线上库
                            //https://api.fentuapp.com.cn/
                            //https://shop.fentuapp.com.cn/
                            port.build()
                        }
                        val newRequest = builder.url(newUrl!!).build()
                        return chain.proceed(newRequest)
                    } else {
                        return chain.proceed(originalRequest)
                    }
                } else {
                    return chain.proceed(originalRequest)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return chain.proceed(originalRequest)
            }

        }
    }

    companion object {
        @Volatile private var retrofit: Retrofit? = null

        private fun createHttpLoggingInterceptor(): Interceptor {
            val logging = HttpLoggingInterceptor(MyLogger())
            logging.level = if (java.lang.Boolean.parseBoolean("true")) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            return logging
        }
    }
}
