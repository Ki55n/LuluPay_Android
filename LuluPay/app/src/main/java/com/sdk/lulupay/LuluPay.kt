package com.sdk.lulupay

import android.content.Context

class LuluPay {

  val TAG: String = "LuluPay"

  fun initializeSDK(context: Context, loginActivity: Class<*>) {}

  fun loginUser(email: String, password: String) {}

  fun initializeRemittance() {}

  fun initializePayment() {}

  fun getTransferHistory() {}

  fun getPaymentHistory() {}
}
