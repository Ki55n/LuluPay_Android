package com.sdk.lulupay.listeners

import com.sdk.lulupay.model.response.*

interface BranchMasterListener {
	fun onSuccess(response: BankBranchResponse)
	fun onFailed(errorMessage: String)
}