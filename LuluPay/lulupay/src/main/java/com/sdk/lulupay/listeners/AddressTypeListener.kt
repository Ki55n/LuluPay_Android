package com.sdk.lulupay.listeners

import com.sdk.lulupay.model.response.CodeResponse

interface AddressTypeListener {
	fun onSuccess(response: CodeResponse)
	fun onFailed(errorMessage: String)
}