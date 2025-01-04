package com.sdk.lulupay.model.response

import java.util.Date
import java.math.BigDecimal

class EnquireTransactionResponse(
    val status: String,
    val statusCode: Int,
    val data: TransactionData
)

data class TransactionData(
    val state: String,
    val subState: String,
    val transactionGmtDate: String,
    val transactionDate: String,
    val type: String,
    val instrument: String,
    val sourceOfIncome: String,
    val purposeOfTxn: String,
    val message: String,
    val sender: Sender,
    val receiver: Receiver,
    val transaction: Transaction
)

data class Sender(
    val mobileNumber: String,
    val customerNumber: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val countryOfBirth: String,
    val gender: String,
    val nationality: String,
    val professionCode: String,
    val employer: String,
    val visaTypeCode: String,
    val senderAddress: List<Address>? = null
)

data class Receiver(
    val agent_receiver_id: String? = null,
    val mobileNumber: String? = null,
    val firstName: String,
    val middleName: String? = null,
    val lastName: String,
    val dateOfBirth: String? = null,
    val gender: String,
    val receiverAddress: List<Address>? = null,
    val nationality: String,
    val relationCode: String,
    val bankDetails: BankDetails? = null,
    val cashPickupDetails: CashPickupDetails? = null
)

data class Address(
    val addressType: String,
    val addressLine: String,
    val streetName: String,
    val buildingNumber: String,
    val postCode: String,
    val pobox: String? = null,
    val townName: String,
    val countrySubdivision: String,
    val countryCode: String
)

data class BankDetails(
    val accountType: String,
    val accountNum: String,
    val isoCode: String,
    val bankId: String,
    val branchId: String,
    val iban: String? = null
)

data class CashPickupDetails(
    val correspondent: String,
    val correspondentId: String,
    val correspondentLocationId: String
)

data class Transaction(
    val quoteId: String,
    val channel_quote_id: String? = null,
    val agent_transaction_ref_number: String? = null,
    val receivingMode: String,
    val paymentMode: String? = null,
    val sendingCountryCode: String,
    val receivingCountryCode: String,
    val sendingCurrencyCode: String,
    val receivingCurrencyCode: String,
    val sendingAmount: BigDecimal,
    val receivingAmount: BigDecimal,
    val totalPayinAmount: BigDecimal,
    val fxRates: List<FxRate2>,
    val feeDetails: List<FeeDetail2>,
    val settlementDetails: List<SettlementDetail2>? = null
)

data class FxRate2(
    val rate: BigDecimal,
    val baseCurrencyCode: String,
    val counterCurrencyCode: String,
    val type: String
)

data class FeeDetail2(
    val type: String,
    val model: String,
    val amount: BigDecimal,
    val description: String? = null,
    val currencyCode: String
)

data class SettlementDetail2(val chargeType: String, val value: BigDecimal, val currencyCode: String)
