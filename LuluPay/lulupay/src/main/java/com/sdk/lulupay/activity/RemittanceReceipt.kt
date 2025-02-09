package com.sdk.lulupay.activity

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.*
import android.util.Log
import androidx.core.content.FileProvider
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sdk.lulupay.R
import com.sdk.lulupay.listeners.*
import com.sdk.lulupay.model.response.*
import com.sdk.lulupay.remittance.Remittance
import com.sdk.lulupay.session.SessionManager
import com.sdk.lulupay.recyclerView.*
import com.sdk.lulupay.database.LuluPayDB
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.material.button.MaterialButton
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import com.google.gson.*
import com.google.gson.JsonObject
import com.rajat.pdfviewer.PdfRendererView
import java.io.File

class RemittanceReceipt : AppCompatActivity() {

   private var transactionRefNo: String = ""
   private lateinit var dialog: AlertDialog
   private lateinit var outputDir: File
	
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_receipt_details)
    
    init()
    showDialogProgress()
    getIntentExtra()
    getTransactionReceipt()
    setClickListener()
    }
    
    private fun init(){
        outputDir = File(getExternalFilesDir(null), transactionRefNo)
        if(outputDir != null && !outputDir.exists()){
            outputDir.mkdirs()
        }
        
        if (outputDir != null) {
            outputDir = File(outputDir.absolutePath, "Receipt.pdf")
            }
    }
    
    private fun getIntentExtra(){
      transactionRefNo = intent.getStringExtra("TRANSACTION_REF_NO") ?: ""
    }
    
    private fun setClickListener(){
      val backBtn: ImageButton = findViewById(R.id.back_button)
      val shareBtn: Button = findViewById(R.id.shareReceiptButton)
      
      backBtn.setOnClickListener{
          finish()
      }
      
      shareBtn.setOnClickListener{
     // val file: File = File(this@RemittanceReceipt.getExternalFilesDir(null),"Receipt.pdf")
       sharePdfFile(outputDir)
          if (outputDir.exists()) {
              val intent = Intent(this, PdfViewScreen::class.java)
              intent.putExtra("pdf_path", outputDir.absolutePath) // Passing the file path
              startActivity(intent)
          } else {
              Toast.makeText(this, "PDF file not found!", Toast.LENGTH_SHORT).show()
          }
      }
    }
    
    private fun getTransactionReceipt(){
      lifecycleScope.launch {
      Remittance.getTransactionReceipt(transactionRefNo = transactionRefNo, listener = object : TransactionReceiptListener{
      override fun onSuccess(response: TransactionReceiptResponse){
            sortReceiptTransactionResponse(response)
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
  
  private fun sharePdfFile(pdfFile: File) {
        // Get the URI for the file using FileProvider
        val fileUri: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            pdfFile
        )

        // Create the intent to share the file
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Start the activity to share the file
        startActivity(Intent.createChooser(shareIntent, "Share PDF using"))
    }

    private fun sortReceiptTransactionResponse(response: TransactionReceiptResponse) {
        val base64EncodedData: String = response.data

        // Decode the Base64 string into a ByteArray
        val pdfByteArray: ByteArray = decodeBase64(base64EncodedData)
        
        if (outputDir != null) {
        if(!outputDir.exists()){
            savePdfFromByteArray(pdfByteArray, outputDir)
            }

            dismissDialogProgress()

            val pdfView: PdfRendererView = findViewById(R.id.pdfView)
            pdfView.initWithFile(outputDir) // ✅ Display PDF in PdfRendererView
            pdfView.visibility = View.VISIBLE
        } else {
            Log.d("RECEIPT", "External storage directory is not available")
        }
    }
  
  private fun showMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
  
  private fun savePdfFromByteArray(data: ByteArray, outputFile: File) {
    try {
        // Write the byte array to the file
        outputFile.writeBytes(data)

        Log.d("RECEIPT", "PDF saved successfully at: ${outputFile.absolutePath}")
    } catch (e: Exception) {
        Log.d("RECEIPT", "Failed to save PDF: ${e.message}")
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
  
  private fun decodeBase64(encodedString: String): ByteArray {
    return Base64.decode(encodedString, Base64.DEFAULT)
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
}