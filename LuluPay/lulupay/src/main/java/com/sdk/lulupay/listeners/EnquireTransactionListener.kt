package com.sdk.lulupay.listeners

import com.sdk.lulupay.model.response.EnquireTransactionResponse

interface EnquireTransactionListener {
      fun onSuccess(response: EnquireTransactionResponse)
  	fun onFailed(errorMessage: String)
}
