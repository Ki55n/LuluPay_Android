package com.sdk.lulupay.token

import com.sdk.lulupay.model.response.AccessTokenResponse
import com.sdk.lulupay.network.client.RetrofitClient
import com.sdk.lulupay.network.interfaces.ApiService
import com.sdk.lulupay.timer.Timer
import java.io.IOException
import org.json.JSONObject

class AccessToken {

  companion object {
    private val apiService = RetrofitClient.retrofit.create(ApiService::class.java)
    var access_token: String = ""

    suspend fun getAccessToken(
        username: String,
        password: String,
        requestId: String,
        grantType: String,
        clientId: String,
        scope: String,
        clientSecret: String
    ): Result<AccessTokenResponse> {
      return try {
        val response =
            apiService.getAccessToken(
                requestId = requestId,
                username = username,
                password = password,
                grantType = grantType,
                clientId = clientId,
                scope = scope,
                clientSecret = clientSecret)

        if (response.isSuccessful) {
          response.body()?.let {
            Timer.start(it.expires_in.toLong(), it.expires_in.toLong())
            access_token = it.access_token
            Result.success(it)
          }
              ?: Result.failure(
                  Exception(
                      "Response body is null (Code: ${response.code()}, Message: ${response.message()})"))
        } else {
          val errorBody = response.errorBody()?.string()
          val errorMessage =
              try {
                JSONObject(errorBody).getString("message")
              } catch (e: Exception) {
                errorBody ?: "Unknown error"
              }
          Result.failure(Exception("Error: $errorMessage"))
        }
      } catch (e: IOException) {
        Result.failure(IOException("Network error: ${e.message}", e))
      } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}", e))
      }
    }
  }
}
