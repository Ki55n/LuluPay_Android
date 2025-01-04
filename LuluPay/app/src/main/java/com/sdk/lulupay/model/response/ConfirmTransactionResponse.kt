package com.sdk.lulupay.model.response

data class ConfirmTransactionRequest(
    val status: String,
    val statusCode: Int,
    val data: TransactionStatusData
)

data class TransactionStatusData(
    val state: String,
    val subState: String,
    val transactionRefNumber: String
)
