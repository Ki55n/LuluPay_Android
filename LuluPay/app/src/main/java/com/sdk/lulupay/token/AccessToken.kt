package com.sdk.lulupay.token

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.sdk.lulupay.model.response.*
import com.sdk.lulupay.network.interfaces.ApiService
import com.sdk.lulupay.network.client.RetrofitClient
import retrofit2.Response

class AccessToken {

  companion object {
    suspend fun getAccessToken(
        username: String,
        password: String,
        requestId: String,
        grantType: String,
        clientId: String,
        scope: String,
        clientSecret: String
    ): Response<AccessTokenResponse> {
      return withContext(Dispatchers.IO) {
        val apiService = RetrofitClient.createRetrofit().create(ApiService::class.java)
        apiService
            .getAccessToken(
                requestId = requestId,
                username = username,
                password = password,
                grantType = grantType,
                clientId = clientId,
                scope = scope,
                clientSecret = clientSecret)
            .execute()
      }
    }
  }
}
