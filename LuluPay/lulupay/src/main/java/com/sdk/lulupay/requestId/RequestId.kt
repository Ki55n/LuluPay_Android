package com.sdk.lulupay.requestId

import java.util.UUID

class RequestId {
  companion object {
    fun generateRequestId(): String {
      return UUID.randomUUID().toString()
    }
  }
}
