package com.sdk.lulupay.model.response

data class AccountValidationResponse(
    val status: String,
    val statusCode: Int,
    val data: String? = null,
    val errorCode: Int? = null,
    val message: String? = null
)
