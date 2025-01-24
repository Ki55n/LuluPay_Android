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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sdk.lulupay.database.LuluPayDB
import com.sdk.lulupay.R
import com.sdk.lulupay.listeners.*
import com.sdk.lulupay.model.response.*
import com.sdk.lulupay.remittance.Remittance
import com.sdk.lulupay.session.SessionManager
import com.google.android.material.button.MaterialButton
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RemittanceDetails : AppCompatActivity() {

    // Header Details
    private lateinit var backButton: ImageButton
    private lateinit var headerAmount: TextView
    private lateinit var headerflagImage: ImageView
    private lateinit var headerCurrencyName: TextView
    
    
    // Body Details
    private lateinit var bodySendingCurrency: TextView
    private lateinit var bodyReceivingCurrency: TextView
    private lateinit var bodyAmount: TextView
    private lateinit var bodyCommissionAmount: TextView
    private lateinit var bodyProcessingFees: TextView
    private lateinit var bodyTotalAmount: TextView
    private lateinit var bodyReference: TextView
    
    // Proceed Button
    private lateinit var proceedBtn: Button

 override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sending_payment_details)
    
    setupViews()
    setClickListener()
  }
  
  private fun setupViews(){
  backButton = findViewById(R.id.back_button)
  headerAmount = findViewById(R.id.amountValue)
  headerflagImage = findViewById(R.id.currencyIcon)
  headerCurrencyName = findViewById(R.id.currencyName)
  
  bodySendingCurrency = findViewById(R.id.sending_currency_code)
  bodyReceivingCurrency = findViewById(R.id.receiving_currency_code)
  bodyAmount = findViewById(R.id.amount)
  bodyCommissionAmount = findViewById(R.id.commission_amount)
  bodyProcessingFees = findViewById(R.id.processing_fees)
  bodyTotalAmount = findViewById(R.id.total_amount)
  bodyReference =findViewById(R.id.reference)
  
  proceedBtn = findViewById(R.id.proceedButton)
  }
  
  private fun setClickListener(){
  backButton.setOnClickListener{
  finish()
  }
  
  proceedBtn.setOnClickListener{
  createTransaction()
  }
  }
  
  private fun createTransaction(){
  
  }

}
