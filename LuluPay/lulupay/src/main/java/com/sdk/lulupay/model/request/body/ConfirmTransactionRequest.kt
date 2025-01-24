package com.sdk.lulupay.model.request.body

data class ConfirmTransactionRequest(
    val transaction_ref_number: String,
    val bank_ref_number: String? = null,
    val customer_bank_name: String? = null,
    val deposit_account_id: String? = null
)
