package com.sdk.lulupay.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.*
import android.content.*
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.sdk.lulupay.R
import com.sdk.lulupay.listeners.*
import com.sdk.lulupay.model.response.*
import com.sdk.lulupay.singleton.ActivityCloseManager
import com.sdk.lulupay.remittance.Remittance
import com.sdk.lulupay.session.SessionManager
import com.sdk.lulupay.requestId.RequestId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine
import java.math.BigDecimal
import com.google.gson.Gson
import com.google.gson.JsonObject

class InputScreen : AppCompatActivity(), FinishActivityListener {

  private lateinit var backButton: ImageButton
  private lateinit var currencyRateValue: TextView
  private lateinit var contactName: TextView
  private lateinit var inputAmountEditText: EditText
  private lateinit var referenceEditText: EditText
  private lateinit var nextButton: Button
  
  // Details gotten from intent extra and will be use to construct remittance payload
  private var sendingCountryCode: String = ""
  private var type: String = ""
  private var receivingMode: String = ""
  private var receivingModeName: String = ""
  private var receivingCountryCode: String = ""
  private var limitMinAmount: BigDecimal = BigDecimal.ZERO
  private var limitPerTransaction: BigDecimal = BigDecimal.ZERO
  private var sendMinAmount: BigDecimal = BigDecimal.ZERO
  private var sendMaxAmount: BigDecimal = BigDecimal.ZERO
  private var correspondent: String? = ""
  private var sendingCurrencyCode: String = ""
  private var sendingCurrencyCode2: String = ""
  private var receivingCurrencyCode: String = ""
  private var receivingCurrencyCode2: String = ""
  private var correspondentName: String = ""
  private var bankId: String? = ""
  private var branchId: String? = ""
  private var branchName: String = ""
  private var routingCode: String? = ""
  private var isoCode: String? = ""
  private var sort: String = ""
  private var iban: String? = ""
  private var bankName: String = ""
  private var bankBranchName: String = ""
  private var ifsc: String = ""
  private var bic: String = ""
  private var address: String = ""
  private var townName: String = ""
  private var countrySubdivision: String = ""
  private var accountNo: String? = ""
  private var firstName: String = ""
  private var middleName: String = ""
  private var lastName: String = ""
  private var instrument: String = ""
  private var phoneNo: String = ""
  private var accountTypeCode: String = ""
  private var reference: String = ""
  
  private var selectedPaymentModeCode = ""
  
  private var rate: BigDecimal = BigDecimal.ZERO
  //private var receivingCurrencyCode: String = ""
  
  private lateinit var dialog: AlertDialog
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_amount)
    
    registerListeners()
    showDialogProgress()
    getIntentExtraData()
    setupViews()
    getRates()
    showMessage("Getting current exchange rate")
  }
  
  private fun registerListeners(){
    ActivityCloseManager.registerListener(this)
  }
  
  private fun destroyListeners(){
   ActivityCloseManager.unregisterListener(this) // Avoid memory leaks
  }
  
  private fun getIntentExtraData() {
    sendingCountryCode = intent.getStringExtra("SENDING_COUNTRY_CODE") ?: ""
    type = intent.getStringExtra("TYPE") ?: ""
    receivingMode = intent.getStringExtra("RECEIVING_MODE") ?: ""
    receivingModeName = intent.getStringExtra("RECEIVING_MODE_NAME") ?: ""
    receivingCountryCode = intent.getStringExtra("RECEIVING_COUNTRY_CODE") ?: ""
    limitMinAmount = getIntentBigDecimal(intent, "LIMIT_MIN_AMOUNT") ?: BigDecimal.ZERO
    limitPerTransaction = getIntentBigDecimal(intent, "LIMIT_PER_TRANSACTION") ?: BigDecimal.ZERO
    sendMinAmount = getIntentBigDecimal(intent, "SEND_MIN_AMOUNT") ?: BigDecimal.ZERO
    sendMaxAmount = getIntentBigDecimal(intent, "SEND_MAX_AMOUNT") ?: BigDecimal.ZERO
    correspondent = intent.getStringExtra("CORRESPONDENT") ?: null
    sendingCurrencyCode = intent.getStringExtra("SENDER_CURRENCY_CODE") ?: ""
    receivingCurrencyCode = intent.getStringExtra("RECEIVER_CURRENCY_CODE") ?: ""
    correspondentName = intent.getStringExtra("CORRESPONDENT_NAME") ?: ""
    bankId = intent.getStringExtra("BANK_ID") ?: null
    branchId = intent.getStringExtra("BRANCH_ID") ?: null
    branchName = intent.getStringExtra("BRANCH_NAME") ?: ""
    routingCode = intent.getStringExtra("ROUTING_CODE") ?: null
    isoCode = intent.getStringExtra("ISO_CODE") ?: null
    sort = intent.getStringExtra("SORT_CODE") ?: ""
    iban = intent.getStringExtra("IBAN") ?: null
    bankName = intent.getStringExtra("BANK_NAME") ?: ""
    bankBranchName = intent.getStringExtra("BANK_BRANCH_NAME") ?: ""
    ifsc = intent.getStringExtra("IFSC") ?: ""
    bic = intent.getStringExtra("BIC") ?: ""
    address = intent.getStringExtra("ADDRESS") ?: ""
    townName = intent.getStringExtra("TOWN_NAME") ?: ""
    countrySubdivision = intent.getStringExtra("COUNTRY_SUBDIVISION") ?: ""
    accountNo = intent.getStringExtra("ACCOUNT_NUMBER") ?: null
    firstName = intent.getStringExtra("RECEIVER_FIRST_NAME") ?: ""
    middleName = intent.getStringExtra("RECEIVER_MIDDLE_NAME") ?: ""
    lastName = intent.getStringExtra("RECEIVER_LAST_NAME") ?: ""
    instrument = intent.getStringExtra("INSTRUMENT") ?: ""
    phoneNo = intent.getStringExtra("RECEIVER_PHONE_NO") ?: ""
    accountTypeCode = intent.getStringExtra("ACCOUNT_TYPE_CODE") ?: ""
}
  
  private fun setupViews() {
  backButton = findViewById(R.id.back_button)
  contactName = findViewById(R.id.contactName)
  inputAmountEditText = findViewById(R.id.inputAmount)
  referenceEditText = findViewById(R.id.referenceInput)
  nextButton = findViewById(R.id.receiptButton)
  currencyRateValue = findViewById(R.id.currency_rate_value)
  
  setClickListeners()
  }
  
  private fun setData(rate: BigDecimal, receivingCurrencyCode: String, sendingCurrencyCode: String){
  firstName = firstName.trim()
  middleName = middleName.trim()
  lastName = lastName.trim()
  contactName.setText("$firstName $middleName $lastName")
  
  val value = 1
  
  currencyRateValue.setText("AED $value = $receivingCurrencyCode $rate")
  }
  
  private fun setClickListeners(){
  backButton.setOnClickListener{
  finish()
  }
  
  nextButton.setOnClickListener{
  val inputAmountValue: String = inputAmountEditText.text.toString()
  
  if(!inputAmountValue.isNullOrEmpty() || !inputAmountValue.contains(",")){
  
  if(referenceEditText?.text.toString().isNullOrEmpty()){
  reference =  "REF" + generateReferenceNo()
  }else{
  if(referenceEditText?.text.toString().length >= 10){
  reference = referenceEditText.text.toString()
  }else{
   showMessage("Reference must be 10 characters or more")
   
  }
  }
  
  if(referenceEditText?.text.toString().length >= 10 || referenceEditText?.text.toString().isNullOrEmpty()){
  showDialogProgress()
  getPaymentMode()
  }
  }else{
  showMessage("Amount is required or Invalid input")
  }
  
  }
  
  inputAmountEditText.addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s?.length!! < 3 || !s.toString().startsWith("AED")) {
            inputAmountEditText.setText("AED")
        }
    }
})
  }
  
  private fun generateReferenceNo(): String {
    return System.currentTimeMillis().toString()
}
  
  private fun getIntentBigDecimal(intent: Intent, key: String): BigDecimal {
    return intent.getStringExtra(key)?.let { BigDecimal(it) } ?: BigDecimal.ZERO
}

private fun getPaymentMode(){
lifecycleScope.launch {
Remittance.getPaymentMode(partnerName = SessionManager.username ?: "",
listener = object : PaymentModeListener {
  override fun onSuccess(response: CodeResponse){
     dismissDialogProgress()
     showDialogWithSpinner(response)
  }
  
  override fun onFailed(errorMessage: String){
  dismissDialogProgress()
  if(isLikelyJson(errorMessage)){
                extractErrorMessageData(errorMessage)
                }else{
                showMessage(errorMessage)
                }
  }
})
}
}

private fun showDialogWithSpinner(response: CodeResponse) {
  lateinit var dialog2: AlertDialog

 val paymentModeList = response.data.payment_modes
   val paymentModeName = mutableListOf("Choose Payment Mode")
   paymentModeName.addAll(paymentModeList.map { it.name })
   
   
    // Inflate the custom layout
    val dialogView = layoutInflater.inflate(R.layout.choose_payment_mode, null)

    // Find the Spinner in the custom layout
    val spinner: Spinner = dialogView.findViewById(R.id.payment_mode_spinner)

    // Create an array adapter for the Spinner
    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentModeName)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = adapter

    // Build the AlertDialog
    dialog2 = AlertDialog.Builder(this)
        .setTitle("Choose Payment Mode")
        .setView(dialogView)
        .setPositiveButton("Proceed") { _, _ ->
            
            if(!selectedPaymentModeCode.isNullOrEmpty() || !selectedPaymentModeCode.isNullOrBlank()){
            dialog2.dismiss()
            showDialogProgress()
            
            // For debug/testing use that
            val closingBalance: Long = 100000 ?: 0
                doLogic(BigDecimal(closingBalance))
            
            // For production use that
            //proceed(paymentMode = selectedPaymentModeCode)
            }else{
            showMessage("Payment mode is required!")
            }
        }
        .setNegativeButton("Cancel", null)
        .create()

    dialog2.show()
    
    spinner.onItemSelectedListener =
    object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>,
            view: View?,
            position: Int,
            id: Long
        ) {
        if(position == 0){
        selectedPaymentModeCode = ""
        return
        }
            if (position - 1 in paymentModeList.indices) { // Ensure valid position
                val selected = paymentModeList[position - 1]
                selectedPaymentModeCode = selected.code
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Handle case where nothing is selected
        }
    }
    
}

private fun proceed(paymentMode: String){
lifecycleScope.launch {
Remittance.getAvailableBalance(
        paymentMode = paymentMode,
        listener = object : AvailableBalanceListener {
            override fun onSuccess(response: AgentCreditBalanceResponse) {
                //val closingBalance: Long = response.data.firstOrNull()?.closingBalance ?: 0
                val closingBalance: Long = 100000 ?: 0
                doLogic(BigDecimal(closingBalance))
            }

            override fun onFailed(errorMessage: String) {
            dismissDialogProgress()
            if(isLikelyJson(errorMessage)){
                extractErrorMessageData(errorMessage)
                }else{
                showMessage(errorMessage)
                }
                }
            })
       }
   }

private fun doLogic(availableBalance: BigDecimal){
val receivingCurrencyValue: BigDecimal = BigDecimal(getAmount(inputAmountEditText.text.toString())).multiply(rate)

val sendingAmountValue: BigDecimal = BigDecimal(getAmount(inputAmountEditText.text.toString()))

if(availableBalance >= sendingAmountValue){
 if (receivingCurrencyValue >= limitMinAmount){
       if (receivingCurrencyValue <= sendMaxAmount){
               if (receivingCurrencyValue >= sendMinAmount) {
                createQuoteTransaction(getAmount(inputAmountEditText.text.toString()))
                }else{
                dismissDialogProgress()
                showMessage("Minimium amount to send is $receivingCurrencyCode2$sendMinAmount")
                }
                }else{
                dismissDialogProgress()
                showMessage("Maximuim amount to send is $receivingCurrencyCode2$sendMaxAmount")
                }
                }else{
                dismissDialogProgress()
                showMessage("limit of a minimium transaction is $receivingCurrencyCode2$limitMinAmount")
                }
                }else{
                showMessage("Insufficient balance")
                }
}

      private fun getAmount(text: String): String {
    val regex = Regex("""\d+([.,]\d+)?""")
    return regex.find(text)?.value ?: ""
}

private fun getRates(){
lifecycleScope.launch {
Remittance.getRate(
receivingCurrencyCode = receivingCurrencyCode, 
receivingCountryCode = receivingCountryCode, 
includeCorrespondents = "True",
receivingMode = receivingMode,
correspondents = correspondent,
listener = object : RatesListener {
   override fun onSuccess(response: RatesResponse){
   dismissDialogProgress()
   sortRatesResponse(response)
   }
   
   override fun onFailed(errorMessage: String){
   dismissDialogProgress()
   if(isLikelyJson(errorMessage)){
                extractErrorMessageData(errorMessage)
                }else{
                showMessage(errorMessage)
                }
   }
})
}
}

private fun sortRatesResponse(response: RatesResponse){
rate = response.data.rates.firstOrNull()?.rate ?: BigDecimal.ZERO

receivingCurrencyCode2 = response.data.rates.firstOrNull()?.to_currency ?: ""

sendingCurrencyCode2 = response.data.rates.firstOrNull()?.from_currency ?: ""

if(receivingCurrencyCode.equals("CNY")){
receivingCurrencyCode2 = "¥"
}

if(receivingCurrencyCode.equals("EGP")){
receivingCurrencyCode2 = "£"
}

if(receivingCurrencyCode.equals("INR")){
receivingCurrencyCode2 = "₹"
}

if(receivingCurrencyCode.equals("PKR")){
receivingCurrencyCode2 = "₨"
}

if(receivingCurrencyCode.equals("PHP")){
receivingCurrencyCode2 = "₱"
}

if(receivingCurrencyCode.equals("LKR")){
receivingCurrencyCode2 = "₨"
}

setData(rate, receivingCurrencyCode2, sendingCurrencyCode2)
}

private fun createQuoteTransaction(amount: String){
lifecycleScope.launch {
Remittance.createQuote(sendingCountryCode = sendingCountryCode, sendingCurrencyCode = sendingCurrencyCode, receivingCountryCode = receivingCountryCode, receivingCurrencyCode = receivingCurrencyCode, sendingAmount = amount, receivingMode = receivingMode, instrument = instrument, paymentMode = selectedPaymentModeCode, isoCode = isoCode, routingCode = routingCode, correspondent = correspondent, correspondentId = bankId, correspondentLocationId = branchId, listener = object : QuoteListener {
    override fun onSuccess(response: QuoteResponse){
     dismissDialogProgress()
     sortQuoteResponse(response)
    }
    
    override fun onFailed(errorMessage: String){
    dismissDialogProgress()
    if(isLikelyJson(errorMessage)){
                extractErrorMessageData(errorMessage)
                }else{
                showMessage(errorMessage)
                }
    }
  })
 }
}

  private fun sortQuoteResponse(response: QuoteResponse){
  val quoteId: String = response.data.quote_id
  val receivingCountryCode: String = response.data.receiving_country_code
  val receivingCurrencyCode: String = response.data.receiving_currency_code
  val sendingCountryCode: String = response.data.sending_country_code
  val sendingCurrencyCode: String = response.data.sending_currency_code
  val sendingAmount: BigDecimal = response.data.sending_amount
  val receivingAmount: BigDecimal = response.data.receiving_amount
  val totalPayinAmount: BigDecimal = response.data.total_payin_amount
  val priceGuarantee: String = response.data.price_guarantee

val gson = Gson()
val intent = Intent(this, RemittanceDetails::class.java)
    intent.putExtra("QOUTE_ID", quoteId)
    intent.putExtra("RECEIVING_MODE_NAME", receivingModeName)
    intent.putExtra("RECEIVING_COUNTRY_CODE", receivingCountryCode)
    intent.putExtra("RECEIVING_CURRENCY_CODE", receivingCurrencyCode)
    intent.putExtra("SENDING_COUNTRY_CODE", sendingCountryCode)
    intent.putExtra("SENDING_CURRENCY_CODE", sendingCurrencyCode)
    intent.putExtra("RECEIVING_CURRENCY_SYMBOL", this.receivingCurrencyCode2)
    intent.putExtra("INSTRUMENT", this.instrument)
    intent.putExtra("SENDING_AMOUNT", sendingAmount.toString())
    intent.putExtra("RECEIVING_AMOUNT", receivingAmount.toString())
    intent.putExtra("TOTAL_PAYIN_AMOUNT", totalPayinAmount.toString())
    intent.putExtra("REFERENCE", reference)
    intent.putExtra("PRICE_GUARANTEE", priceGuarantee)
    intent.putExtra("ACCOUNT_NO", accountNo)
    intent.putExtra("ISO_CODE", isoCode)
    intent.putExtra("IBAN", iban)
    intent.putExtra("ROUTING_CODE", routingCode)
    intent.putExtra("RECEIVING_MODE", receivingMode)
    intent.putExtra("CORRESPONDENT", correspondent)
    intent.putExtra("BANK_ID", bankId)
    intent.putExtra("BRANCH_ID", branchId)
    intent.putExtra("QUOTE_ID", quoteId)
    intent.putExtra("RECEIVER_PHONE_NO", phoneNo)
    intent.putExtra("RECEIVER_FIRST_NAME", firstName)
    intent.putExtra("RECEIVER_MIDDLE_NAME", middleName)
    intent.putExtra("RECEIVER_LAST_NAME", lastName)
    intent.putExtra("ACCOUNT_TYPE_CODE", accountTypeCode)
    intent.putExtra("FX_RATES", gson.toJson(response.data.fx_rates))
    intent.putExtra("FEE_DETAILS", gson.toJson(response.data.fee_details))
    intent.putExtra("SETTLEMENT_DETAILS", gson.toJson(response.data.settlement_details))
    intent.putExtra("CORRESPONDENT_RULES", gson.toJson(response.data.correspondent_rules))
startActivity(intent)
  }
  
  private fun showMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  private fun showDialogProgress() {
    // Build the AlertDialog
    dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
        .setView(R.layout.custom_dialog) // Set custom layout as the dialog's content
        .setCancelable(false) // Disable back button dismiss
        .create()

    // Prevent dialog from dismissing on outside touch
    dialog.setCanceledOnTouchOutside(false)

    // Show the dialog
    dialog.show()
}

  private fun dismissDialogProgress() {
    if (dialog.isShowing == true) {
      dialog.dismiss()
    }
  }
  
  private fun isLikelyJson(input: String): Boolean {
    return input.trimStart().startsWith('{') || input.trimStart().startsWith('[')
}
  
  private fun extractErrorMessageData(errorMessage: String){
    val gson = Gson()

    // Parse the JSON string into a JsonObject
    val jsonObject = gson.fromJson(errorMessage, JsonObject::class.java)

    // Extract the "message" value
    val message = jsonObject.get("message").asString
    
    showMessage(message)
  }
  
  override fun onFinishActivity() {
        finish() // Close this activity when called
    }
    
  override fun onDestroy() {
        super.onDestroy()
        destroyListeners()
    }
}
