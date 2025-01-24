package com.sdk.lulupay.listeners

interface BottomSheetListener {
fun onBottomSheetDismissed(bankId: String, branchId: String, branchName: String, branchFullName: String, countryCode: String, ifsc: String, bic: String, bankName: String, routingCode: String, swiftCode: String, sortCode: String, address: String, townName: String, countrySubdivision: String)
}
