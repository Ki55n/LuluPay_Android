package com.sdk.lulupay.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.*
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.activity.OnBackPressedCallback
import com.sdk.lulupay.R
import com.sdk.lulupay.listeners.*
import com.sdk.lulupay.model.response.*
import com.sdk.lulupay.remittance.Remittance
import com.sdk.lulupay.session.SessionManager
import com.sdk.lulupay.singleton.ActivityCloseManager
import com.sdk.lulupay.recyclerView.*
import com.sdk.lulupay.database.LuluPayDB
import com.google.android.material.button.MaterialButton
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import com.google.gson.*
import com.google.gson.reflect.TypeToken

class RemittanceSuccessScreen : AppCompatActivity(){
     
 private lateinit var backButton: ImageButton
 private lateinit var paymentMessageText: TextView
 private lateinit var receiptButton: Button
 
 private var firstName: String = ""
 private var middleName: String = ""
 private var lastName: String = ""
 private var receivingCurrencySymbol: String = ""
 private var receivingCurrencyCode: String = ""
 private var receivingAmount: String = ""
 private var transactionRefNo: String = ""
     
	override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_payment_done)
    
    destroyPreviousActivity()
    registerEvents()
    getIntentExtras()
    setupViews()
    setClickListeners()
    setData()
  }
  
  private fun destroyPreviousActivity(){
    ActivityCloseManager.closeAll()
  }
  
  private fun registerEvents(){
      onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle back button press
                goBack()
            }
        })
    }
  
  private fun setupViews(){
  backButton = findViewById(R.id.back_button)
  paymentMessageText = findViewById(R.id.paymentMessageText)
  receiptButton = findViewById(R.id.receiptButton)
  }
  
  private fun getIntentExtras(){
   firstName = intent.getStringExtra("RECEIVER_FIRST_NAME") ?: ""
  middleName = intent.getStringExtra("RECEIVER_MIDDLE_NAME") ?: ""
  lastName = intent.getStringExtra("RECEIVER_LAST_NAME") ?: ""
  receivingCurrencyCode = intent.getStringExtra("RECEIVING_CURRENCY_CODE") ?: ""
  receivingCurrencySymbol = intent.getStringExtra("RECEIVING_CURRENCY_SYMBOL") ?: ""
  receivingAmount = intent.getStringExtra("RECEIVING_AMOUNT") ?: ""
  transactionRefNo = intent.getStringExtra("TRANSACTION_REF_NO") ?: ""
  }
  
  private fun setClickListeners(){
  backButton.setOnClickListener{
  goBack()
  }
  
  receiptButton.setOnClickListener{
  redirectToReceiptScreen()
  }
  }
  
  private fun setData(){
   paymentMessageText.setText("You’ve successfully sent $receivingCurrencySymbol $receivingAmount $receivingCurrencyCode to $firstName $middleName $lastName")
  }
  
  private fun redirectToReceiptScreen(){
    val intent = Intent(this, RemittanceReceipt::class.java)
    intent.putExtra("TRANSACTION_REF_NO", transactionRefNo)
       startActivity(intent)
  }
  
  private fun goBack(){
    val intent = Intent(this, RemittanceScreen::class.java)
    intent.putExtra("ISMANUALLOGIN", true)
       startActivity(intent)
       finish()
  }
}