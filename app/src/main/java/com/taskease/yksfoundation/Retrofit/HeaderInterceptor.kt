package com.taskease.yksfoundation.Retrofit

import com.taskease.yksfoundation.Constant.SharedPreferenceManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val newRequest: Request = originalRequest.newBuilder()
            .addHeader("Authorization", SharedPreferenceManager.getString(SharedPreferenceManager.TOKEN))
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(newRequest)
    }
}