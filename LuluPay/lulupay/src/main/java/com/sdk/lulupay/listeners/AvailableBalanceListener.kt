package com.sdk.lulupay.listeners

import com.sdk.lulupay.model.response.AgentCreditBalanceResponse

interface AvailableBalanceListener {
   fun onSuccess(response: AgentCreditBalanceResponse)
   fun onFailed(errorMessage: String)
}
