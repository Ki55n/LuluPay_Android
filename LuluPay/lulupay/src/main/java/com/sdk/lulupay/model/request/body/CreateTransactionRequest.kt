package com.sdk.lulupay.model.request.body

import java.math.BigDecimal

data class CreateTransactionRequest(
    val type: String,
    val source_of_income: String,
    val purpose_of_txn: String,
    val instrument: String,
    val message: String? = null,
    val sender: Sender,
    val receiver: Receiver,
    val transaction: Transaction,
)

data class Sender(val customer_number: String, val agent_customer_number: String? = null)

data class Receiver(
    val agent_receiver_id: String? = null,
    val mobile_number: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val middle_name: String? = null,
    val date_of_birth: String? = null,
    val gender: String? = null,
    val receiver_address: List<ReceiverAddress>,
    val receiver_id: List<String> = emptyList(),
    val nationality: String? = null,
    val relation_code: String,
	val name: String? = null,
	val type_of_business: String? = null,
    val bank_details: BankDetails? = null,
    val mobileWallet_details: MobileWalletDetails? = null,
    val agent_receiver_number: String? = null // For the last form
)

data class ReceiverAddress(
    val address_type: String,
    val address_line: String? = null,
    val street_name: String? = null,
    val building_number: String? = null,
    val post_code: String? = null,
    val pobox: String? = null,
    val town_name: String? = null,
    val country_subdivision: String? = null,
    val country_code: String
)

data class BankDetails(
    val account_type_code: String,
    val account_number: String? = null,
    val iso_code: String? = null,
    val iban: String,
	val bank_id: String? = null,
	val branch_id: String? = null,
	val routing_code: String? = null,
	val sort_code: String? = null
)

data class MobileWalletDetails(
    val iso_code: String, val wallet_id: String, 
	val iban: String,
	val bank_id: String? = null,
	val branch_id: String? = null,
	val routing_code: String? = null,
	val sort_code: String? = null)

data class Transaction(
    val quote_id: String? = null,
	val agent_transaction_ref_number: String? = null,
	val receiving_mode: String? = null,
	val sending_country_code: String? = null,
	val sending_currency_code: String? = null,
	val receiving_country_code: String? = null,
	val receiving_currency_code: String? = null,
	val sending_amount: BigDecimal? = null,
	val receiving_amount: BigDecimal? = null,
	val payment_mode: String? = null,
    val fx_rates: List<FxRate>? = null,
    val fee_details: List<FeeDetail>? = null
)

data class FxRate(
    val rate: BigDecimal,
    val base_currency_code: String,
    val counter_currency_code: String,
    val type: String
)

data class FeeDetail(
    val type: String,
    val currency_code: String,
    val amount: BigDecimal,
    val description: String
)
