package com.sdk.lulupay.model.response

import java.math.BigDecimal

data class RatesResponse(val status: String, val statusCode: Int, val data: RateData)

data class RateData(val rates: List<Rate>)

data class Rate(
    val rate: BigDecimal,
    val toCurrencyName: String,
    val toCurrency: String,
    val fromCurrency: String,
    val toCountryName: String,
    val toCountry: String,
    val receivingMode: String,
    val correspondent: String? = null,
    val anywhere: Int? = null,
    val correspondentName: String? = null
)
