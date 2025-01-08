package com.sdk.lulupay.model.response

class TransactionReceiptResponse(
    val status: String,
    val status_code: Int,
    val data: String // Base64 data as String
)