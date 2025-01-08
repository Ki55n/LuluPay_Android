package com.sdk.lulupay.model.response

import java.math.BigDecimal

data class QuoteResponse(val data: QuoteData2? = null)

data class QuoteData2(val status: String,
val statusCode: Int,
val data: QuoteData
)

data class QuoteData(
val quoteId: String,
val createdAt: String,
val createdAtGmt: String,
val expiresAt: String,
val expiresAtGmt: String,
val receivingCountryCode: String,
val receivingCurrencyCode: String,
val sendingCountryCode: String,
val sendingCurrencyCode: String,
val sendingAmount: BigDecimal,
val receivingAmount: BigDecimal,
val totalPayinAmount: BigDecimal,
val fxRates: List<FxRate>,
val feeDetails: List<FeeDetail>,
val settlementDetails: List<SettlementDetail>,
val correspondentRules: List<CorrespondentRule>,
val priceGuarantee: String
)

data class FxRate(
val cost_rate: BigDecimal? = null,
val rate: BigDecimal,
val baseCurrencyCode: String,
val counterCurrencyCode: String,
val type: String
)

data class FeeDetail(
val type: String,
val model: String,
val currencyCode: String,
val amount: BigDecimal,
val description: String? = null
)

data class SettlementDetail(
val chargeType: String,
val value: BigDecimal,
val currencyCode: String
)

data class CorrespondentRule(
val field: String? = null,
val rule: String? = null
)