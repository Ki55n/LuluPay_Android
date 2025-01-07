package com.sdk.lulupay.network.interfaces

// import com.sdk.lulupay.model.request.body.ConfirmTransactionRequest
// import com.sdk.lulupay.model.response.ConfirmTransactionResponse
import com.sdk.lulupay.model.request.body.*
import com.sdk.lulupay.model.response.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

  // Access & Privilage Api
  @FormUrlEncoded
  @POST("auth/realms/cdp/protocol/openid-connect/token")
  fun getAccessToken(
      @Header("Content-Type")
      contentType: String =
          "application/x-www-form-urlencoded", // Default to 'application/x-www-form-urlencoded'
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Field("username") username: String,
      @Field("password") password: String,
      @Field("grant_type") grantType: String,
      @Field("client_id") clientId: String,
      @Field("scope") scope: String,
      @Field("client_secret") clientSecret: String
  ): Call<AccessTokenResponse>

  // Remittance/Transfer Api
  @POST("api/v1_0/paas/quote")
  fun createQuote(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String, // Sender info
      @Header("channel") channel: String, // Channel info
      @Header("company") company: String, // Company info
      @Header("branch") branch: String, // Branch info
      @Body request: QuoteRequest // Request body
  ): Call<QuoteResponse>

  @POST("api/v1_0/paas/createtransaction")
  fun createTransaction(
      @Header("Authorization") authorization: String,
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String? = null,
      @Header("channel") channel: String? = null,
      @Header("company") company: String? = null,
      @Header("branch") branch: String? = null,
      @Body request: CreateTransactionRequest
  ): Call<CreateTransactionResponse>

  @POST("/api/v1_0/ras/confirmtransaction")
  fun confirmTransaction(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String? = null, // Sender info
      @Header("channel") channel: String? = null, // Channel info
      @Header("company") company: String? = null, // Company info
      @Header("branch") branch: String? = null, // Branch info
      @Body payload: ConfirmTransactionRequest // Request payload
  ): Call<ConfirmTransactionResponse>

  @POST("api/v1_0/paas/authorize-clearance")
  fun authorizeClearance(
      @Header("Authorization") authorization: String,
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String? = null,
      @Header("channel") channel: String? = null,
      @Header("company") company: String? = null,
      @Header("branch") branch: String? = null,
      @Body request: AuthorizationClearanceRequest
  ): Call<AuthorizationClearanceResponse>

  @GET("api/v1_0/paas/enquire-transaction")
  fun enquireTransaction(
      @Header("Authorization") authorization: String,
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String? = null,
      @Header("channel") channel: String? = null,
      @Header("company") company: String? = null,
      @Header("branch") branch: String? = null,
      @Query("transaction_ref_number") transaction_ref_number: String
  ): Call<EnquireTransactionResponse>

  @POST("api/v1_0/ras/brn-update")
  fun updateBrn(
      @Header("Authorization") authorization: String,
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String? = null,
      @Header("channel") channel: String? = null,
      @Header("company") company: String? = null,
      @Header("branch") branch: String? = null,
      @Body request: BrnUpdateRequest
  ): Call<BrnUpdateResponse>

  @GET("api/v1_0/ras/transaction-receipt")
  fun getTransactionReceipt(
      @Header("Authorization") authorization: String,
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String? = null,
      @Header("channel") channel: String? = null,
      @Header("company") company: String? = null,
      @Header("branch") branch: String? = null,
      @Query("transaction_ref_number") transaction_ref_number: String
  ): Call<TransactionReceiptResponse>

  @POST("api/v1_0/ras/canceltransaction")
  fun cancelTransaction(
      @Header("Authorization") authorization: String,
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String? = null,
      @Header("channel") channel: String? = null,
      @Header("company") company: String? = null,
      @Header("branch") branch: String? = null,
      @Body request: CancelTransactionRequest
  ): Call<CancelTransactionResponse>

  @PUT("api/v1_0/paas/status-update")
  fun updateStatus(
      @Header("Authorization") authorization: String,
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String? = null,
      @Header("channel") channel: String? = null,
      @Header("company") company: String? = null,
      @Header("branch") branch: String? = null,
      @Body request: StatusUpdateRequest
  ): Call<StatusUpdateResponse>

  // Callback Api
  // Code here. It will be hosted by partner

  // Master Api
  @GET("raas/masters/v1/codes")
  fun getCodes(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String, // Sender info
      @Header("channel") channel: String, // Channel info
      @Header("company") company: String, // Company info
      @Header("branch") branch: String, // Branch info
      @Query("code") code: String? = null, // Query parameter (optional)
      @Query("service_type") service_type: String? = null // Query parameter (optional)
  ): Call<CodeResponse>

  @GET("raas/masters/v1/service-corridor")
  fun getServiceCorridor(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String, // Sender info
      @Header("channel") channel: String, // Channel info
      @Header("company") company: String, // Company info
      @Header("branch") branch: String, // Branch info
      @Query("receiving_mode") receiving_mode: String? = null, // Query parameter (optional)
      @Query("receiving_country_code")
      receiving_country_code: String? = null // Query parameter (optional)
  ): Call<ServiceCorridorResponse>

  @GET("raas/masters/v1/banks")
  fun getMasterBanks(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Query("receiving_country_code") countryCode: String, // Required
      @Query("receiving_mode") receivingMode: String? = null, // Optional
      @Query("correspondent") correspondent: String? = null, // Optional
      @Query("page") page: Int? = null, // Optional
      @Query("size") size: Int? = null, // Optional
      @Query("bank_id") bank_id: String? = null, // Optional
      @Query("bank_name") bank_name: String? = null // Query parameter (optional)
  ): Call<MasterBankResponse>

  @GET("raas/masters/v1/banks/{bankId}")
  fun getMasterBankById(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Query("correspondent") correspondent: String? = null, // Query parameter (optional)
      @Path("bankId") bankId: String // Bank ID as a path parameter
  ): Call<MasterBankDetailResponse>

  @GET("raas/masters/v1/banks/{bankId}/branches")
  fun getBankBranches(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Path("bankId") bankId: String, // Bank ID as a path parameter
      @Query("receiving_country_code") receivingCountryCode: String,
      @Query("receiving_mode") receivingMode: String,
      @Query("page") page: Int,
      @Query("size") size: Int,
      @Query("correspondent") correspondent: String,
      @Query("branch_id") branch_id: String? = null, // Optional
      @Query("branch_name_part") branch_name_part: String? = null, // Not Mandatory
  ): Call<BankBranchResponse>

  @GET("raas/masters/v1/banks/{bankId}/branches/{branchId}")
  fun getBranchDetails(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Path("bankId") bankId: String, // Bank ID as a path parameter
      @Path("branchId") branchId: String, // Branch ID as a path parameter
      @Query("correspondent") correspondent: String? // Optional query parameter
  ): Call<BranchDetailsResponse>

  @GET("raas/masters/v1/branches/lookup")
  fun searchBranch(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Header("sender") sender: String, // Sender info
      @Header("company") company: String, // Company info
      @Header("branch") branch: String, // Branch info
      @Query("receiving_country_code") receivingCountryCode: String, // Query parameter
      @Query("iso_code") isoCode: String? = null,
      @Query("page") page: Int? = null,
      @Query("size") size: Int? = null,
      @Query("correspondent") correspondent: String? = null,
      @Query("receiving_mode") receiving_mode: String? = null,
      @Query("routing_code") routing_code: String? = null,
      @Query("sort_code") code: String? = null
  ): Call<BranchSearchResponse>

  @GET("raas/masters/v1/accounts/balance")
  fun getAgentCreditBalance(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Query("payment_mode") payment_mode: String? = null
  ): Call<AgentCreditBalanceResponse>

  @GET("raas/masters/v1/rates")
  fun getRates(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Query("receiving_currency_code") receiving_currency_code: String? = null,
      @Query("receiving_country_code") receiving_country_code: String? = null,
      @Query("include_correspondents") include_correspondents: String? = null,
      @Query("receiving_mode") receiving_mode: String? = null,
      @Query("correspondent") correspondent: String? = null
  ): Call<RatesResponse>

  @GET("raas/masters/v1/accounts/validation")
  fun validateAccount(
      @Header("Authorization") authorization: String, // Bearer token
      @Header("X-REQUEST-ID") requestId: String, // Unique request ID
      @Query("receiving_country_code") receivingCountryCode: String, // Query parameter
      @Query("receiving_mode") receivingMode: String, // Query parameter
      @Query("correspondent") correspondent: String? = null, // Query parameter
      @Query("iso_code") iso_code: String? = null, // Query parameter
      @Query("routing_code") routing_code: Int? = null, // Query parameter
      @Query("sort_code") sort_code: String? = null, // Query parameter
      @Query("account_number") account_number: String? = null, // Query parameter
      @Query("iban") iban: String? = null, // Query parameter
      @Query("bank_id") bank_id: String? = null, // Query parameter
      @Query("branch_id") branch_id: String? = null, // Query parameter
      @Query("first_name") first_name: String? = null, // Query parameter
      @Query("middle_name") middle_name: String? = null, // Query parameter
      @Query("last_name") last_name: String // Query parameter
  ): Call<AccountValidationResponse>
}
