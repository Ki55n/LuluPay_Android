package com.sdk.lulupay

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.sdk.lulupay.model.response.*
import com.sdk.lulupay.network.interfaces.ApiService
import com.sdk.lulupay.network.client.RetrofitClient
import com.sdk.lulupay.listeners.*

class LuluPay {

companion object{
  val TAG: String = "LuluPay"

  fun initializeSDK(context: Context, loginActivity: Class<*>) {}

  fun loginUser(
      username: String,
      password: String,
	  requestId: String,
	  grantType: String,
	  clientId: String,
	  scope: String,
	  clientSecret: String,
	  listener: AccessTokenListener
  ) {
    CoroutineScope(Dispatchers.IO).launch {
	try {
	val apiService = RetrofitClient.createRetrofit().create(ApiService::class.java)
	val response = apiService.getAccessToken(
	requestId = requestId,
	username = username,
	password = password,
	grantType = grantType,
	clientId = clientId,
	scope = scope,
	clientSecret = clientSecret
	).execute()
	
	if (response.isSuccessful) {
	response.body()?.let {
	// Notify success
	CoroutineScope(Dispatchers.Main).launch {
	listener.onSuccess(it)
	}
	} ?: run {
	// Handle null body
	CoroutineScope(Dispatchers.Main).launch {
	listener.onFailure("Response body is null")
	}
	}
	} else {
	CoroutineScope(Dispatchers.Main).launch {
	listener.onFailure("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
	}
	}
	} catch (e: Exception) {
	CoroutineScope(Dispatchers.Main).launch {
	listener.onFailure("Exception: ${e.message ?: "Unknown exception"}")
	}
	}
	}
      }

  fun initializeRemittance() {}

  fun initializePayment() {}

  fun getTransferHistory() {}

  fun getPaymentHistory() {}
	  }
}
