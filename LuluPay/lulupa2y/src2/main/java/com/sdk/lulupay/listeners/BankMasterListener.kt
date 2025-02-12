package com.sdk.lulupay.listeners

import com.sdk.lulupay.model.response.MasterBankResponse

interface BankMasterListener {
  fun onSuccess(response: MasterBankResponse)

  fun onFailed(errorMessage: String)
}
