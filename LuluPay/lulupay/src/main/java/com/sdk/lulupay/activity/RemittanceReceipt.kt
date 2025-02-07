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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sdk.lulupay.R
import com.sdk.lulupay.listeners.*
import com.sdk.lulupay.model.response.*
import com.sdk.lulupay.recyclerView.*
import com.ymg.pdf.viewer.PDFView
import com.google.android.material.button.MaterialButton
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import com.google.gson.*
import com.google.gson.JsonObject
import java.io.File

class RemittanceReceipt : AppCompatActivity() {

   private var transactionRefNo: String = ""
   private lateinit var dialog: AlertDialog
	
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_receipt_details)
    
    showDialogProgress()
    getIntentExtra()
    getTransactionReceipt()
    setClickListener()
    }
    
    private fun getIntentExtra(){
      transactionRefNo = intent.getStringExtra("TRANSACTION_REF_NO") ?: ""
    }
    
    private fun setClickListener(){
        var shareBtn:Button = findViewById(R.id.shareReceiptButton)
      
      shareBtn.setOnClickListener{
      val file: File = File(this@RemittanceReceipt.getExternalFilesDir(null),"Receipt.pdf")
       sharePdfFile(file)
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

    // Get the external files directory and create a File object for the PDF
    val outputDir: File? = getExternalFilesDir(null)
    if (outputDir != null) {
        val outputFile = File(outputDir, "Receipt.pdf")
        savePdfFromByteArray(pdfByteArray, outputFile)
        
        loadPdf(outputDir.absolutePath)
    } else {
        Log.d("RECEIPT", "External storage directory is not available")
    }
}
  
  private fun loadPdf(outputFile: String){
    PDFView pdfView = findViewById(R.id.pdfView)
    pdfView.fromAsset(outputFile)
    .enableSwipe(true)
    .swipeHorizontal(false)
    .enableDoubletap(true)
    .defaultPage(0)
    .onLoad(new OnLoadCompleteListener() {
        @Override
        public void loadComplete(int nbPages) {
            // Handle loading completion
            dismissDialogProgress()
        }
    })
    .load()
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