package com.sdk.lulupay.remittance

import com.sdk.lulupay.listeners.*
import com.sdk.lulupay.model.request.body.*
import com.sdk.lulupay.network.client.RetrofitClient
import com.sdk.lulupay.network.interfaces.ApiService
import com.sdk.lulupay.requestId.RequestId
import com.sdk.lulupay.session.SessionManager
import com.sdk.lulupay.timer.Timer
import com.sdk.lulupay.token.AccessToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class Remittance {
  companion object {
  suspend fun createQuote(sendingCountryCode: String, sendingCurrencyCode: String, receivingCountryCode: String, receivingCurrencyCode: String, sendingAmount: String, receivingMode: String, instrument: String, paymentMode: String, correspondent: String, correspondentId: String?, correspondentLocationId: String?, listener: QuoteListener){
  try{
  val apiService = RetrofitClient.retrofit.create(ApiService::class.java)
    val request = QuoteRequest(
        sending_country_code = sendingCountryCode,
        sending_currency_code = sendingCurrencyCode,
        receiving_country_code = receivingCountryCode,
        receiving_currency_code = receivingCurrencyCode,
        sending_amount = BigDecimal(sendingAmount),
        receiving_amount = null,
        receiving_mode = receivingMode,
        type = "SEND",
        instrument = instrument,
        payment_mode = paymentMode,
        correspondent = correspondent,
        correspondent_id = correspondentId,
        correspondent_location_id = correspondentLocationId
    )

         // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }
            
            
            val response =
            withContext(Dispatchers.IO) {
              apiService
                  .createQuote(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      request = request)
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
            
  }catch(e: Exception){
  listener.onFailed("Unexpected error: ${e.message}")
  }
  }
  
  suspend fun getRate(receivingCurrencyCode: String, receivingCountryCode: String, includeCorrespondents: String, receivingMode: String, correspondents: String?, listener: RatesListener){
  try{
  val apiService = RetrofitClient.retrofit.create(ApiService::class.java)
  
  
         // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }
            
            
            val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getRates(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      receiving_currency_code = receivingCurrencyCode,
                      receiving_country_code = receivingCountryCode,
                      include_correspondents = includeCorrespondents,
                      receiving_mode = receivingMode,
                      correspondent = correspondents)
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
  
  }catch(e: Exception){
  listener.onFailed("Unexpected error: ${e.message}")
  }
  }
  
  
  suspend fun getAvailableBalance(paymentMode: String, listener: AvailableBalanceListener){
  try{
  val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }
            
            
            val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getAgentCreditBalance(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      payment_mode = paymentMode)
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
  }catch(e: Exception){
  listener.onFailed("Unexpected error: ${e.message}")
  }
  }
  
  suspend fun branchLookup(sortCode: String? = null, routingCode: String? = null, swiftCode: String? = null, partnerName: String, receivingCountryCode: String, receivingMode: String, listener: BranchLookupListener){
  try{
  val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .searchBranch(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      receiving_mode = receivingMode,
                      receivingCountryCode = receivingCountryCode,
                      correspondent = null,
                      code = sortCode,
                      isoCode = swiftCode,
                      routing_code = routingCode)
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
  }
    suspend fun validateAccount(
        partnerName: String,
        receiving_country_code: String,
        receiving_mode: String,
        swiftCode: String? = null,
        routingCode: Int? = null,
        iban: String? = null,
        acct_no: String? = null,
        first_name: String,
        middle_name: String,
        last_name: String,
        listener: ValidateAccountListener
    ) {
      try {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .validateAccount(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      receiving_mode = receiving_mode,
                      receiving_country_code = receiving_country_code,
                      iso_code = swiftCode,
                      routing_code = routingCode,
                      iban = iban,
                      account_number = acct_no,
                      first_name = first_name,
                      middle_name = middle_name,
                      last_name = last_name)
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }

    suspend fun getBranchMaster(
        receiving_country_code: String,
        receiving_mode: String,
        correspondent: String,
        bankId: String,
        partnerName: String,
        listener: BranchMasterListener
    ) {
      try {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getBankBranches(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      receivingMode = receiving_mode,
                      receivingCountryCode = receiving_country_code,
                      correspondent = correspondent,
                      page = 1,
                      size = 1,
                      bankId = bankId)
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }

    suspend fun getServiceCorridor(
        partnerName: String,
        receiving_mode: String,
        receiving_country_code: String,
        listener: ServiceCorridorListener
    ) {
      try {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getServiceCorridor(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      receiving_mode = receiving_mode,
                      receiving_country_code = receiving_country_code)
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }

    suspend fun getInstruments(partnerName: String, listener: InstrumentListener) {
      try {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getCodes(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      code = "INSTRUMENTS",
                      service_type = "C2C")
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }

    suspend fun getAddressType(partnerName: String, listener: AddressTypeListener) {
      try {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getCodes(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      code = "ADDRESS_TYPES",
                      service_type = "C2C")
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }

    suspend fun getAccountType(partnerName: String, listener: AccountTypeListener) {
      try {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getCodes(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      code = "ACCOUNT_TYPES",
                      service_type = "C2C")
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }

    suspend fun getCorrespondent(partnerName: String, listener: CorrespondentListener) {
      try {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getCodes(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      code = "CORRESPONDENTS",
                      service_type = "C2C")
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }

    suspend fun getSourceOfIncome(partnerName: String, listener: SourceOfIncomeListener) {
      try {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getCodes(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      code = "SOURCE_OF_INCOMES",
                      service_type = "C2C")
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }

    suspend fun getPaymentMode(partnerName: String, listener: PaymentModeListener) {
      try {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getCodes(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      code = "PAYMENT_MODES",
                      service_type = "C2C")
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }

    suspend fun getPurposeOfTXN(partnerName: String, listener: PurposeOfTXNListener) {
      try {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "password",
                      clientId = SessionManager.clientId ?: "cdp_app",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret
                              ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getCodes(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      code = "PURPOSE_OF_TRANSACTIONS",
                      service_type = "C2C")
                  .execute()
            }

        if (response.isSuccessful) {
          val responses = response.body()
          if (responses != null) {
            listener.onSuccess(responses)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }

    suspend fun getReceivingModes(partnerName: String, listener: ReceiveModeListener) {
      try {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "",
                      clientId = SessionManager.clientId ?: "",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret ?: "")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getCodes(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      sender = SessionManager.username ?: "",
                      code = "RECEIVING_MODES",
                      service_type = "C2C")
                  .execute()
            }

        if (response.isSuccessful) {
          val receiveModes = response.body()
          if (receiveModes != null) {
            listener.onSuccess(receiveModes)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }

    suspend fun getBankMaster(
        countryCode: String,
        receivingMode: String,
        partnerName: String,
        listener: BankMasterListener
    ) {
      try {
        // Get the API service
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        // Get the access token
        val token: String =
            if (!Timer.isRunning) {
              val result =
                  AccessToken.getAccessToken(
                      username = SessionManager.username ?: "",
                      password = SessionManager.password ?: "",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      grantType = SessionManager.grantType ?: "",
                      clientId = SessionManager.clientId ?: "",
                      scope = SessionManager.scope,
                      clientSecret = SessionManager.clientSecret ?: "")

              val error = result.exceptionOrNull()
              if (error != null) {
                listener.onFailed(error.message ?: "Error occurred: Null")
                return
              }

              val newToken = result.getOrNull()?.access_token
              if (newToken.isNullOrEmpty()) {
                listener.onFailed("Access token is null or empty")
                return
              }

              AccessToken.access_token = newToken // Cache the token
              newToken
            } else {
              AccessToken.access_token
            }

        // Make the API call with coroutines
        val response =
            withContext(Dispatchers.IO) {
              apiService
                  .getMasterBanks(
                      authorization = "Bearer $token",
                      requestId = (SessionManager.username ?: "") + "-" + RequestId.generateRequestId(),
                      countryCode = countryCode,
                      receivingMode = receivingMode)
                  .execute()
            }

        if (response.isSuccessful) {
          val bankData = response.body()
          if (bankData != null) {
            listener.onSuccess(bankData)
          } else {
            listener.onFailed("Response body is null")
          }
        } else {
          listener.onFailed("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
        }
      } catch (e: Exception) {
        listener.onFailed("Unexpected error: ${e.message}")
      }
    }
  }
}
