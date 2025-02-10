package com.sdk.lulupay.activity

import android.content.Intent
import android.util.Log
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sdk.lulupay.token.AccessToken
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sdk.lulupay.R
import com.sdk.lulupay.database.*
import com.sdk.lulupay.listeners.*
import com.sdk.lulupay.model.response.*
import com.sdk.lulupay.session.SessionManager
import com.sdk.lulupay.remittance.Remittance
import com.sdk.lulupay.recyclerView.*
import com.sdk.lulupay.requestId.RequestId
import com.sdk.lulupay.singleton.ActivityCloseManager
import com.sdk.lulupay.storage.SecureLoginStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.JsonObject

class TransactionHistoryScreen : AppCompatActivity(){

  private lateinit var backBtn: ImageButton
  private lateinit var transactionState: TextView
  private lateinit var transactionSubState: TextView
  private lateinit var transactionDate: TextView
  private lateinit var senderName: TextView
  private lateinit var senderCustomerNo: TextView
  private lateinit var receiverName: TextView
  private lateinit var receiverPhoneNo: TextView
  private lateinit var receiverRefNo: TextView
  private lateinit var transactionAmount: TextView
  
  private lateinit var dialog: AlertDialog
  
  private var transactionRefNo: String = ""
    
 override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_remittance_history)
    
    getIntentExtra()
    showDialog()
    setupViews()
    setupClickListener()
    getEnquireTransaction()
 }
 
 private fun getIntentExtra(){
     transactionRefNo = intent.getStringExtra("TRANSACTION_REF_NO") ?: ""
 }
 
 private fun setupViews(){
     backBtn = findViewById(R.id.back_button)
     transactionState = findViewById(R.id.tvTransactionState)
     transactionSubState = findViewById(R.id.tvTransactionSubState)
     transactionDate = findViewById(R.id.tvTransactionDate)
     senderName = findViewById(R.id.tvSenderName)
     senderCustomerNo = findViewById(R.id.tvSenderCustomerNumber)
     receiverName = findViewById(R.id.tvReceiverName)
     receiverPhoneNo = findViewById(R.id.tvReceiverMobile)
     receiverRefNo = findViewById(R.id.tvTransactionRefNo)
     transactionAmount = findViewById(R.id.tvTransactionAmount)
     
 }
 
 private fun setupClickListener(){
     backBtn.setOnClickListener{
         finish()
     }
 }
 
 private fun getEnquireTransaction(){
      lifecycleScope.launch {
        Remittance.enquireTransaction(transactionRefNo = transactionRefNo, listener = object : EnquireTransactionListener{
           override fun onSuccess(response: EnquireTransactionResponse){
            sortEnquireTransactionResponse(response)
            dismissDialog()
           }
           
           override fun onFailed(errorMessage: String){
            dismissDialog()
            if(isLikelyJson(errorMessage)){
                extractErrorMessageData(errorMessage)
                }else{
                showMessage(errorMessage)
                }
           }
        })
      }
    }
    
  fun isLikelyJson(input: String): Boolean {
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
  
  private fun showMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
    
    private fun sortEnquireTransactionResponse(response: EnquireTransactionResponse){
        transactionState.setText("State: " + response.data.state)
        transactionSubState.setText("Sub-State" + response.data.sub_state)
        transactionDate.setText("Date: " + response.data.transaction_date)
        senderName.setText("Sender: " + response.data.sender?.first_name + " " + response.data.sender?.last_name)
        senderCustomerNo.setText("Customer No: " + response.data.sender?.customer_number)
        receiverName.setText("Receiver: " + response.data.receiver.first_name + " " + response.data.receiver.middle_name + " " + response.data.receiver.last_name)
        receiverPhoneNo.setText("Mobile: " + response.data.receiver.mobile_number)
        receiverRefNo.setText("Ref No: " + response.data.transaction.agent_transaction_ref_number)
        transactionAmount.setText("Amount: " + response.data.transaction.sending_currency_code + " " + response.data.transaction.sending_amount + " → " + response.data.transaction.receiving_currency_code + " " + response.data.transaction.receiving_amount)
    }
 
 private fun showDialog() {
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

  private fun dismissDialog() {
    if (dialog.isShowing == true) {
      dialog.dismiss()
    }
}
}