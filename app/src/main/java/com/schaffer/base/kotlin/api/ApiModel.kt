package com.schaffer.base.kotlin.api

import com.schaffer.base.kotlin.common.base.BaseModel



/**
 * @author : SchafferWang at AndroidSchaffer
 * @date  : 2018/3/14
 * Project : SchafferBaseLibrary_Kotlin
 * Package : com.schaffer.base.kotlin.api
 * Description :
 */
class ApiModel private constructor() : BaseModel<ApiService>() {

    override val serviceClass: Class<ApiService>
        get() = ApiService::class.java

    companion object {

        @Volatile private var apiModel: ApiModel? = null

        val instance: ApiModel?
            get() {
                if (apiModel == null) {
                    synchronized(ApiModel::class.java) {
                        if (apiModel == null) {
                            apiModel = ApiModel()
                        }
                    }
                }
                return apiModel
            }
    }

    //    //所有retrofit 参数赋值给retrofit接口实现
    //    public  Observable<bean> methodName(int x,int y,...){
    //
    //        return getService().methodName(.....);
    //    }
}