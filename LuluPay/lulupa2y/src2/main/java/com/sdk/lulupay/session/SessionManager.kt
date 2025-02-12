package com.sdk.lulupay.session

object SessionManager {
  var username: String? = null
  var password: String? = null
  var grantType: String? = null
  var clientId: String? = null
  var scope: String? = null
  var clientSecret: String? = null
  var partnerName: String? = null

  fun clearSession() {
    username = null
    password = null
    grantType = null
    clientId = null
    scope = null
    clientSecret = null
  }
}
