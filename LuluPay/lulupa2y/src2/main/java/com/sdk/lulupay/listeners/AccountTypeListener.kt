package com.sdk.lulupay.listeners

import com.sdk.lulupay.model.response.CodeResponse

interface AccountTypeListener {
	fun onSuccess(response: CodeResponse)
	fun onFailed(errorMessage: String)
}