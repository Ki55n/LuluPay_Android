package com.sdk.lulupay.listeners

import com.sdk.lulupay.model.response.*

interface AccessTokenListener {
	fun onSuccess(response: AccessTokenResponse)
	fun onFailure(error: String)
}