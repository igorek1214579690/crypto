package com.example.crypto.data.remote



import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-CMC_PRO_API_KEY", apiKey)
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}