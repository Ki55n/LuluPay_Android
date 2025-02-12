package com.sdk.lulupay.listeners;

import com.sdk.lulupay.model.response.*

interface BranchLookupListener {
  fun onSuccess(response: BranchSearchResponse)
  fun onFailed(errorMessage: String)
}