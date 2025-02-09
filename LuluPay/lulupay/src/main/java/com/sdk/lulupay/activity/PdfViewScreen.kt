package com.sdk.lulupay.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.rajat.pdfviewer.PdfRendererView
import com.sdk.lulupay.R
import java.io.File

class PdfViewScreen : AppCompatActivity() {
    private lateinit var pdfView: PdfRendererView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view_screen)

        pdfView = findViewById(R.id.pdfView)

        val filePath = intent.getStringExtra("pdf_path")

        if (filePath != null) {
            val file = File(filePath)
            if (file.exists()) {
                pdfView.initWithFile(file) // Load the local PDF file
            } else {
                Log.e("PDF Viewer", "File does not exist: $filePath")
            }
        } else {
            Log.e("PDF Viewer", "No file path received!")
        }
    }
}
