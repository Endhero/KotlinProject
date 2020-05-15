package com.lcd.kotlinproject.utils.http

import com.baronzhang.retrofit2.converter.FastJsonConverterFactory
import com.lcd.kotlinproject.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit service generator
 * Created by Chen on 2016/7/1.
 */
object ServiceGenerator {
    private var httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    
    private fun getRetrofitBuilder(host: String): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(host)
            .addConverterFactory(FastJsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }

    fun <S> createService(serviceClass: Class<S>?): S {
        val retrofit = getRetrofitBuilder(BuildConfig.HOST).client(
            httpClient
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
        ).build()
        return retrofit.create(serviceClass)
    }

    fun <S> createService(host: String, serviceClass: Class<S>?): S {
        val retrofit = getRetrofitBuilder(host).client(
            httpClient
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
        ).build()
        return retrofit.create(serviceClass)
    }

    init {
        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            )
        }
        httpClient.authenticator(TokenAuthenticator())
    }
}