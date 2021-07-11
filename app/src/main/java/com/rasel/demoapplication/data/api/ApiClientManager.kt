package com.rasel.demoapplication.data.api

import com.rasel.demoapplication.BuildConfig
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.CookieManager
import java.util.concurrent.TimeUnit

open class ApiClientManager {

    companion object {
        private val cookieJar = JavaNetCookieJar(CookieManager())

        val pexelApiService: PexelApiService
            get() = Retrofit.Builder()
                .client(getClient())
                .baseUrl(BuildConfig.PEXEL_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(PexelApiService::class.java)

        private fun getClient(): OkHttpClient {

            return OkHttpClient
                .Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .addInterceptor {
                    val response = it.proceed(
                        it.request()
                            .newBuilder()
                            .addHeader("Authorization", "563492ad6f91700001000001938c24e943a74c82904f73b0d1de1c70")
                            .build()
                    )
                    response
                }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .cookieJar(cookieJar)
                .build()
        }
    }
}