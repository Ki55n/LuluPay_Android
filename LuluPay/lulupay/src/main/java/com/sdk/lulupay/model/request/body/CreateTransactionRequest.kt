package com.sdk.lulupay.model.request.body

import java.math.BigDecimal

data class CreateTransactionRequest(
    val type: String,
    val source_of_income: String,
    val purpose_of_txn: String,
    val instrument: String,
    val message: String,
    val sender: Sender,
    val receiver: Receiver,
    val transaction: Transaction,
)

data class Sender(val customer_number: String, val agent_customer_number: String? = null)

data class Receiver(
    val agent_receiver_id: String? = null,
    val mobile_number: String,
    val first_name: String,
    val last_name: String,
    val middle_name: String,
    val date_of_birth: String? = null,
    val gender: String? = null,
    val receiver_address: List<ReceiverAddress>? = null,
    val receiver_id: List<String>? = null,
    val nationality: String,
    val relation_code: String,
	val name: String? = null,
	val type_of_business: String? = null,
    val bank_details: BankDetails? = null,
    val mobileWallet_details: MobileWalletDetails? = null,
    val cashPickup_details: CashPickupDetails? = null,
    val agent_receiver_number: String? = null // For the last form
)

data class ReceiverAddress(
    val address_type: String? = null,
    val address_line: String? = null,
    val street_name: String? = null,
    val building_number: String? = null,
    val post_code: String? = null,
    val pobox: String? = null,
    val town_name: String? = null,
    val country_subdivision: String? = null,
    val country_code: String? = null
)

data class BankDetails(
    val account_type_code: String? = null,
    val account_number: String? = null,
    val iso_code: String? = null,
    val iban: String? = null,
	val routing_code: String? = null,
)

data class CashPickupDetails(
    val correspondent_id: String? = null,
    val correspondent: String? = null,
    val correspondent_location_id: String? = null
)

data class MobileWalletDetails(
    val wallet_id: String? = null, 
    val correspondent: String? = null,
	val bank_id: String? = null,
	val branch_id: String? = null)

data class Transaction(
    val quote_id: String,
	val agent_transaction_ref_number: String,
	val receiving_mode: String? = null,
	val sending_country_code: String? = null,
	val sending_currency_code: String? = null,
	val receiving_country_code: String? = null,
	val receiving_currency_code: String? = null,
	val sending_amount: BigDecimal? = null,
	val receiving_amount: BigDecimal? = null,
	val payment_mode: String? = null,
)