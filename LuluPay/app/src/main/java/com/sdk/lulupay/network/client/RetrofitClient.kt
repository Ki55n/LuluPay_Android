package com.sdk.lulupay.network.client

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

  private const val BASE_URL = "https://drap-sandbox.digitnine.com/"

  // Logging Interceptor
  private val loggingInterceptor =
      HttpLoggingInterceptor().apply {
        level =
            HttpLoggingInterceptor.Level
                .BODY // You can choose different levels: NONE, BASIC, HEADERS, BODY
      }

  // Interceptor to add Content-Type header
  private val headerInterceptor = Interceptor { chain ->
    val request = chain.request().newBuilder().addHeader("Content-Type", "application/json").build()
    chain.proceed(request)
  }

  // OkHttp Client with interceptors
  private val okHttpClient = OkHttpClient.Builder()
  .addInterceptor(headerInterceptor) // Adds the Content-Type header
  .addInterceptor(loggingInterceptor) // Logs the request/response
  .build()

  val retrofit: Retrofit =
      Retrofit.Builder()
          .baseUrl(BASE_URL)
          .client(okHttpClient)
          .addConverterFactory(GsonConverterFactory.create())
          .build()
}
