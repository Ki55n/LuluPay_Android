package com.sdk.lulupay.listeners

import com.sdk.lulupay.model.response.*

interface ConfirmTransactionListener {
    fun onSuccess(response: ConfirmTransactionResponse)
    fun onFailed(errorMessage: String)
}
